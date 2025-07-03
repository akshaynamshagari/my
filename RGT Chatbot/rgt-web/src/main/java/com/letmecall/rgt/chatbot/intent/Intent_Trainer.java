package com.letmecall.rgt.chatbot.intent;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.letmecall.rgt.chatbot.language.LanguageMapper;

import opennlp.tools.doccat.DoccatFactory;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.ObjectStreamUtils;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;

@Service
public class Intent_Trainer {

	private static final Logger logger = LoggerFactory.getLogger(Intent_Trainer.class);
	
	private LanguageMapper languageMapper;
	
	@Autowired
	public Intent_Trainer(LanguageMapper languageMapper) {
		this.languageMapper = languageMapper;
	}

	@Value("${chatbot.path.bot_model_file_path}")
	private String bot_model_file_path;

	@Value("${chatbot.path.bot_model_sentence_file_path}")
	private String bot_model_sentence_file_path;

	@Value("${chatbot.path.bot_model_nlp_token_file_path}")
	private String bot_model_nlp_token_file_path;

	@Value("${chatbot.path.bot_model_nlp_pos_lemma_file_path}")
	private String bot_model_nlp_pos_lemma_file_path;

	@Value("${chatbot.path.bot_model_nlp_lemma_file_path}")
	private String bot_model_nlp_lemma_file_path;

	@Value("${chatbot.path.bot_model_nlp_pos_file_path}")
	private String bot_model_nlp_pos_file_path;

	public void train(String filePathToTrain, String modelPath, String language)
			throws FileNotFoundException, IOException {
		logger.debug("train:INVOKED");
		File trainingDirectory = new File(filePathToTrain);
		
		String languageCode = languageMapper.getLanguageKey(language);
		if (languageCode.equalsIgnoreCase("en")) {
			languageCode = "en";
		}

		if (!trainingDirectory.isDirectory()) {
			throw new IllegalArgumentException(
					"TrainingDirectory is not a directory: " + trainingDirectory.getAbsolutePath());
		}

		List<ObjectStream<DocumentSample>> categoryStreams = new ArrayList<ObjectStream<DocumentSample>>();
		for (File trainingFile : trainingDirectory.listFiles()) {
			String intent = trainingFile.getName().replaceFirst("[.][^.]+$", "");
			ObjectStream<String> lineStream = new PlainTextByLineStream(
					new MarkableFileInputStreamFactory(trainingFile), "UTF-8");
			ObjectStream<DocumentSample> documentSampleStream = new IntentDocumentSampleStream(intent, lineStream);
			categoryStreams.add(documentSampleStream);
		}

		ObjectStream<DocumentSample> combinedDocumentSampleStream = ObjectStreamUtils
				.concatenateObjectStream(categoryStreams);

		TrainingParameters trainingParams = new TrainingParameters();
		trainingParams.put(TrainingParameters.ITERATIONS_PARAM, 10);
		trainingParams.put(TrainingParameters.CUTOFF_PARAM, 0);

		DoccatModel doccatModel = DocumentCategorizerME.train(languageCode, combinedDocumentSampleStream,
				trainingParams, new DoccatFactory());
		combinedDocumentSampleStream.close();

		File modelFile = new File(bot_model_file_path);
		try (OutputStream modelOut = new BufferedOutputStream(new FileOutputStream(modelFile))) {
			doccatModel.serialize(modelOut);
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("Model training complete. Saved to: " + modelFile.getAbsolutePath());
	}

	public String message(String message) throws IOException {
		logger.debug("message:INVOKED");
		File modelFile = new File(bot_model_file_path);
		if (!modelFile.exists()) {
			modelFile.createNewFile();
		}
		DoccatModel model = new DoccatModel(modelFile);

		DocumentCategorizerME categorizer = new DocumentCategorizerME(model);

		String[] nlpLemma = new String[] {};
		for (String sentence : nlpSentences(message)) {
			String[] nlpTokens = nlpTokens(sentence);
			nlpLemma = nlpLemma(nlpTokens);
		}

		double[] outcomes = categorizer.categorize(message.split(" "));
//		double[] out = categorizer.categorize(nlpLemma);
		double threshold = 0.15;

		String intent = categorizer.getBestCategory(outcomes);
		double asDouble = Arrays.stream(outcomes).max().getAsDouble();
		
		if (asDouble < threshold) {
			intent = "unknown";
		}
		
//		Map<String, Double> scoreMap = categorizer.scoreMap(message.split(" "));
		logger.info("Predicted Intent: " + intent);
		return intent;
	}

	private String[] nlpSentences(String input) throws IOException {
		InputStream isentence = new FileInputStream(bot_model_sentence_file_path);

		SentenceModel model = new SentenceModel(isentence);

		SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
		String sentences[] = sentenceDetector.sentDetect(input);
		return sentences;
	}

	private String[] nlpTokens(String sentence) throws IOException {
		InputStream itoken = new FileInputStream(bot_model_nlp_token_file_path);

		TokenizerME myCategorizer = new TokenizerME(new TokenizerModel(itoken));

		String[] Ttokens = myCategorizer.tokenize(sentence);

		// Stop Words Removal
		Set<String> stopWords = new HashSet<>(Arrays.asList("a", "an", "the", "is", "are", "was", "were", "in", "on",
				"at", "for", "to", "of", "and", "or", "but", "if", "then", "so", "because", "as", "with", "about"));

		List<String> filteredTokensList = new ArrayList<>();
		for (String token : Ttokens) {
//			if (!stopWords.contains(token.toLowerCase())) { // Remove stop words (case insensitive)
			filteredTokensList.add(token);
//			}
		}
		String[] tokens = filteredTokensList.toArray(new String[0]);
		return tokens;
	}

	private String[] nlpLemma(String[] tokens) throws IOException {
		InputStream posisLema = new FileInputStream(bot_model_nlp_pos_lemma_file_path);
		POSModel posModelLema = new POSModel(posisLema);
		POSTaggerME posLema = new POSTaggerME(posModelLema);
		String partsOfSpeechLema[] = posLema.tag(tokens);

		InputStream lemmais = new FileInputStream(bot_model_nlp_lemma_file_path);
		DictionaryLemmatizer lemmatizer = new DictionaryLemmatizer(lemmais);
		String[] lemmas = lemmatizer.lemmatize(tokens, partsOfSpeechLema);
		return lemmas;
	}

	private String[] POS(String[] tokens) throws IOException {
		InputStream posis = new FileInputStream(bot_model_nlp_pos_file_path);
		POSModel posModel = new POSModel(posis);
		POSTaggerME pos = new POSTaggerME(posModel);
		String partsOfSpeech[] = pos.tag(tokens);
		return partsOfSpeech;
	}

}
