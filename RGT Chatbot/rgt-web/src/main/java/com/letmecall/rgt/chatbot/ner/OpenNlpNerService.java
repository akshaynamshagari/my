package com.letmecall.rgt.chatbot.ner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.letmecall.rgt.chatbot.model.Entity;
import com.letmecall.rgt.chatbot.model.EntityRequest;
import com.letmecall.rgt.chatbot.ner.tokeninzer.RealLifeChatTokenizer;
import com.letmecall.rgt.chatbot.ner.tokeninzer.Tokenizer;
import com.letmecall.rgt.chatbot.ner.tools.CompactDataEntry;
import com.letmecall.rgt.chatbot.ner.tools.CompactDataHandler;
import com.letmecall.rgt.chatbot.ner.tools.CustomDataHandler;
import com.letmecall.rgt.chatbot.ner.tools.OpenNlpDataHandler;
import com.letmecall.rgt.chatbot.ner.trainers.NerTrainer;
import com.letmecall.rgt.chatbot.ner.trainers.OpenNlpNerTrainer;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.Span;

@Service
public class OpenNlpNerService {
	
	private static final Logger logger = LoggerFactory.getLogger(OpenNlpNerService.class);

	private EntityRequest entityRequestMain = new EntityRequest();
	
	@Value("${chatbot.path.bot_text_NER_compact_file_path}")
	private String bot_text_NER_compact_file_path;
	
	@Value("${chatbot.path.bot_model_NER_NLP_file_path}")
	private String bot_model_NER_NLP_file_path;
	
	@Value("${chatbot.path.bot_model_NER_NLP_TRAIN_file_path}")
	private String bot_model_NER_NLP_TRAIN_file_path;
	
	@Value("${chatbot.path.bot_model_NER_NLP_FOLDER}")
	private String bot_model_NER_NLP_FOLDER;
	
	
	public void trainEntities(EntityRequest entityRequest) throws Exception {
		logger.debug("trainEntities:INVOKED");
	    String nerCompactTrainDataFile = bot_text_NER_compact_file_path;
	    String nerTrainerPropertiesFile = null;
	    String nerModelFileBase = bot_model_NER_NLP_file_path; 
	    String nerTrainFileBase = bot_model_NER_NLP_TRAIN_file_path; 

	    File folder = new File(bot_model_NER_NLP_FOLDER);

	    // Delete old train and model files
	    File[] files = folder.listFiles((dir, name) -> 
	        name.startsWith("opennlp_model_ner") || 
	        name.startsWith("opennlp_train_ner") || 
	        name.startsWith("nerCompactTrain"));

	    if (files != null) {
	        for (File file : files) {
	            if (file.delete()) {
	            	logger.info("Deleted: " + file.getName());
	            } else {
	            	logger.info("Failed to delete: " + file.getName());
	            }
	        }
	    }

	    if (entityRequest.getData().isEmpty()) {
	        entityRequestMain = new EntityRequest();
	        return;
	    }

	    List<String> dataList = entityRequest.getData();
	    
	    try (BufferedWriter writer = new BufferedWriter(new FileWriter(nerCompactTrainDataFile, false))) {
			for (String line : dataList) {
				writer.write(line);
				writer.newLine();
			}
		} catch (IOException e) {
			System.err.println("Error writing to file: " + e.getMessage());
		}
	    
	    List<String> type = entityRequest.getType();
	    String[] labels = type.toArray(new String[0]); // Convert list to array
	    String languageCode = "en";

	    // Import and prepare training data
	    Collection<CompactDataEntry> trainData = CustomDataHandler.importCompactData(nerCompactTrainDataFile);
	    Tokenizer tokenizer = new RealLifeChatTokenizer("", "", "SEP");
	    CompactDataHandler cdh = new OpenNlpDataHandler();
	    
//	    LanguageDetectors language = new LanguageDetectors();
//	    LanguageMapper languageMapper = new LanguageMapper();

	    // Create separate train and model files for each label
	    for (String label : labels) {

	        // Extract only data related to the current label
	        List<String> filteredTrainData = cdh.importTrainDataNer(trainData, tokenizer, false, label);
	        List<String> filteredLabelData = new ArrayList<>();
	        if (!filteredTrainData.isEmpty()) {
	        	for (String data : filteredTrainData) {
//	        		String detectLanguage = language.detectLanguage(data.split("---")[0]);
//	        		String language2 = languageMapper.getLanguage(detectLanguage.toLowerCase());
		        	if (data.contains(label)) {
//		        		boolean contains = entityRequest.getLanguage().contains(language2);
//		        		if (contains) {
			        		filteredLabelData.add(data);
//							languageCode = detectLanguage;
//						}
					}
				}
			}
	        
	        String nerTrainFile = nerTrainFileBase + "_" + label + "_" + languageCode;
//	        String nerModelFile = nerModelFileBase + "_" + label + "_" + languageCode;
	        
	        if (!filteredLabelData.isEmpty()) {
	            // Write training data for the specific label
	            CustomDataHandler.writeTrainData(nerTrainFile, filteredLabelData);

	            // Train a model for this specific label
	            NerTrainer trainer = new OpenNlpNerTrainer(nerTrainerPropertiesFile, nerTrainFileBase, nerModelFileBase, new String[]{label}, languageCode);
	            trainer.train();
	            logger.info("Model Tarined on "+label);
	        } else {
	        	logger.info("No training data for label: " + label + ", skipping.");
	        }
	        filteredLabelData.clear();
	    }

	    entityRequestMain = entityRequest;
	}


	public List<Entity> getEntities(String text) throws IOException {
		logger.debug("getEntities:INVOKED");
	    text = text.replaceAll(",", "");

	    SimpleTokenizer tokenizerS = SimpleTokenizer.INSTANCE;
	    String[] tokens = tokenizerS.tokenize(text);

	    File modelFolder = new File(bot_model_NER_NLP_FOLDER); // Adjust if necessary
	    File[] modelFiles = modelFolder.listFiles((dir, name) -> name.startsWith("opennlp_model_ner"));

	    if (modelFiles == null || modelFiles.length == 0) {
	    	logger.info("No NER models found. Please train the model first.");
	        return new ArrayList<>();
	    }

	    List<Entity> entityList = new ArrayList<>();

	    // Iterate over all available models
	    for (File modelFile : modelFiles) {
	        try (InputStream inputStream = new FileInputStream(modelFile)) {
	            TokenNameFinderModel model = new TokenNameFinderModel(inputStream);
	            NameFinderME nameFinderME = new NameFinderME(model);

	            Span[] spans = nameFinderME.find(tokens);
	            for (Span span : spans) {
	                String entityName = String.join(" ", Arrays.copyOfRange(tokens, span.getStart(), span.getEnd()));
	                entityList.add(new Entity(entityName, span.getType())); // Store entity with its type
	            }
	        } catch (IOException e) {
	            System.err.println("Error loading model: " + modelFile.getName());
	            e.printStackTrace();
	        }
	    }

	    return entityList;
	}


	public EntityRequest getAllEntities() {
		logger.debug("getAllEntities:INVOKED");
		String filePath = bot_text_NER_compact_file_path;
		Map<String, String> entities = new HashMap<>();
		File file = new File(filePath);

		if (!file.exists()) {
			logger.info("File not found: " + filePath);
			return new EntityRequest();
		}

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			List<String> dataList = new ArrayList<>();
			Set<String> typeList = new LinkedHashSet<>();

			while ((line = reader.readLine()) != null) {
				String capitalWords = extractCapitalWords(line);
				if (!capitalWords.isEmpty()) {
					dataList.add(line);
					typeList.add(capitalWords);
					entities.put(capitalWords, line);
				}
			}

			entityRequestMain.setData(dataList);
			entityRequestMain.setType(new ArrayList<String>(typeList));

		} catch (IOException e) {
			System.err.println("Error reading file: " + e.getMessage());
		}

		return entityRequestMain;
	}

	private String extractCapitalWords(String text) {
		logger.debug("extractCapitalWords:INVOKED");
		Set<String> uniqueCapitalWords = new LinkedHashSet<>();
		String[] words = text.split("\\s+");

		for (String word : words) {
			if (word.matches("[A-Z]+") && word.length() > 1) {
				uniqueCapitalWords.add(word);
			}
		}

		String result = String.join(" ", uniqueCapitalWords).trim();

		return result.isEmpty() ? null : result;
	}

}
