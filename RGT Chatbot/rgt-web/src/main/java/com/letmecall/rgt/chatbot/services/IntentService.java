package com.letmecall.rgt.chatbot.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.letmecall.rgt.chatbot.intent.Intent_Trainer;
import com.letmecall.rgt.chatbot.language.LanguageDetectors;
import com.letmecall.rgt.chatbot.language.LanguageMapper;
import com.letmecall.rgt.chatbot.model.ChatBot;
import com.letmecall.rgt.chatbot.model.Entity;
import com.letmecall.rgt.chatbot.model.EntityRequest;
import com.letmecall.rgt.chatbot.model.Intent;
import com.letmecall.rgt.chatbot.model.IntentRequest;
import com.letmecall.rgt.chatbot.model.Request;
import com.letmecall.rgt.chatbot.model.Response;
import com.letmecall.rgt.chatbot.ner.OpenNlpNerService;


@Service
public class IntentService {
	
	private static final Logger logger = LoggerFactory.getLogger(IntentService.class);

	@Autowired
	private Intent_Trainer trainer;
	
	@Autowired
	private LanguageDetectors language;
	
	@Autowired
	private LanguageMapper languageMapper;
	
	private final DbMessageService dbMessageService;
	
	private final ObjectMapper objectMapper;
	
	private HashSet<String> languagesEnabled = new HashSet<String>();

	@Autowired
	private OpenNlpNerService openNlpNerService;
	
	@Value("${chatbot.path.bot_intent_file_path}")
	private String bot_intent_file_path;
	
	@Value("${chatbot.path.bot_model_file_path}")
	private String bot_model_file_path;
	
	@Value("${chatbot.path.bot_json_file_path}")
	private String bot_json_file_path;
	

	public IntentService(ObjectMapper objectMapper, DbMessageService dbMessageService) {
		this.dbMessageService = dbMessageService;
		this.objectMapper = objectMapper;
	}
	
	private static String reduceRepeats(String word) {
        Pattern repeatPattern = Pattern.compile("(\\w)\\1{2,}");
        Matcher matcher = repeatPattern.matcher(word);

        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1)); 
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

	public String intentRecognize(Request request) {
		logger.debug("intentRecognize:INVOKED");
		String userInput = reduceRepeats(request.getInput().toLowerCase());
		String userResp = "";
		List<Response> intentIdentification = new ArrayList<>();
		try {
			intentIdentification = fetchingUserResponse(userInput);
		} catch (IOException e) {
			e.printStackTrace();
			userResp = "File Not Found Error";
		}

		for (int i = 0; i < intentIdentification.size(); i++) {
			if (i == 0 && !intentIdentification.isEmpty()) {
				Response response = intentIdentification.get(i);
				userResp = response.getContent().getText();
			}
		}
		ChatBot chatbot = new ChatBot();
		chatbot.setInput(request.getInput());
		chatbot.setSessionId(request.getSessionId());
		chatbot.setResponse(userResp);
		dbMessageService.saveMessage(chatbot);
		return userResp;
	}

	public List<Response> fetchingUserResponse(String userInput) throws IOException {
		logger.debug("fetchingUserResponse:INVOKED");

		String detectLanguage = language.detectLanguage(userInput);
		String predictedLanguge= languageMapper.getLanguage(detectLanguage.toLowerCase());

		String userIntent = trainer.message(userInput);
		List<Response> intentIdentification = intentIdentification(userIntent);

		Pattern pattern = Pattern.compile("\\{(.*?)\\}");

		userInput = userInput.replaceAll("[^a-zA-Z0-9\\s]", "");

		List<Entity> nerCustomize = openNlpNerService.getEntities(userInput);
		String text = "";

//		for (String entity : languagesEnabled) {
//			System.out.println(entity);
//		}

		for (Response response : intentIdentification) {
			text = response.getContent().getText();
			Matcher matcher = pattern.matcher(text);

			while (matcher.find()) {
				String placeholder = matcher.group(1);
//				System.out.println("Found Placeholder: " + placeholder);

				if (containsPlaceholder(text)) {
					for (Entity ent : nerCustomize) {
						if (ent.getType().startsWith(placeholder)) {
							text = text.replaceAll("\\{[^{}]+\\}", ent.getValue());
						}
					}
					text = text.replaceAll("\\{[^{}]+\\}", "");
				}

			}
			response.getContent().setText(text);
		}
		return intentIdentification;
	}

	public List<Response> intentIdentification(String intentString) throws IOException {
		logger.debug("intentIdentification:INVOKED");
		List<Response> responses = new ArrayList<>();
		for (Intent intent : getIntents()) {
			if (intent.getIntentName().equals(intentString)) {
				responses.addAll(intent.getResponses());
				break;
			}
		}
		return responses;
	}

	public boolean containsPlaceholder(String text) {
		logger.debug("containsPlaceholder:INVOKED");
		Pattern pattern = Pattern.compile("\\{([^{}]+)\\}");
		Matcher matcher = pattern.matcher(text);

		return matcher.find();
	}

	public String addIntent(IntentRequest intentRequest) {
		logger.debug("addIntent:INVOKED");
		String userResp = "";
		Intent newIntent = new Intent();
		newIntent.setIntentName(intentRequest.getIntentName());
		newIntent.setPatterns(intentRequest.getPatterns());
		newIntent.setResponses(intentRequest.getResponses());
		newIntent.setLanguage(intentRequest.getLanguage());

		List<Intent> intents;
		try {
			intents = getIntents();
			if (intentExistCheck(intents, newIntent)) {
				return updateIntent(intentRequest);
			} 
//			else {
//				intents = new ArrayList<>();
//			}

			intents.add(newIntent);
			saveIntents(intents);

			fileWrite(newIntent);
			trainingTheBot(newIntent.getLanguage());
			userResp = "Sucessfully added the intent";
		} catch (IOException e) {
			e.printStackTrace();
			userResp = "Error writing the files";
		}
		return userResp;
	}

	public String updateIntent(IntentRequest intentRequest) {
		logger.debug("updateIntent:INVOKED");
		String userResp = "";
		Intent updatedIntent = new Intent();
		updatedIntent.setIntentName(intentRequest.getIntentName());
		updatedIntent.setPatterns(intentRequest.getPatterns());
		updatedIntent.setResponses(intentRequest.getResponses());
		updatedIntent.setLanguage(intentRequest.getLanguage());

		try {
			List<Intent> intents = getIntents();
			boolean found = false;

			for (int i = 0; i < intents.size(); i++) {
				languagesEnabled.add(intents.get(i).getLanguage().toLowerCase());
				if (intents.get(i).getIntentName().equalsIgnoreCase(updatedIntent.getIntentName())) {
					intents.set(i, updatedIntent);
					found = true;
					break;
				}
			}

			if (!found) {
				userResp = "Intent with name '" + updatedIntent.getIntentName() + "' not found.";
				return userResp;
			}

			saveIntents(intents);

			for (Intent intent : intents) {
				if (updatedIntent.getIntentName().equalsIgnoreCase(intent.getIntentName())) {
					fileWrite(updatedIntent);
				}
			}

			trainingTheBot(updatedIntent.getLanguage());
			userResp = "Sucessfully updated the intent";
		} catch (IOException e) {
			e.printStackTrace();
			userResp = "Error writing the files";
		}
		return userResp;

	}

	private void fileWrite(Intent intent) throws IOException {
		logger.debug("fileWrite:INVOKED");
		File checkFile = new File(bot_intent_file_path + intent.getIntentName() + ".txt");
		FileWriter writer = null;
		if (checkFile.exists()) {
			writer = new FileWriter(bot_intent_file_path + intent.getIntentName() + ".txt", false);

			writer.write("Intent" + " " + "Text" + System.lineSeparator());
			for (String pattern : intent.getPatterns()) {
				writer.write(intent.getIntentName() + " " + pattern + System.lineSeparator());
			}
		} else {
			checkFile.createNewFile();
			writer = new FileWriter(bot_intent_file_path + intent.getIntentName() + ".txt", false);
			writer.write("Intent" + " " + "Text" + System.lineSeparator());
			for (String pattern : intent.getPatterns()) {
				writer.write(intent.getIntentName() + " " + pattern + System.lineSeparator());
			}
		}
		writer.close();
	}

	private void trainingTheBot(String intentLanguage) throws FileNotFoundException, IOException {
		logger.debug("trainingTheBot:INVOKED");
		trainer.train(bot_intent_file_path, bot_model_file_path, intentLanguage);
	}

	public boolean intentExistCheck(List<Intent> intents, Intent newIntent) {
		logger.debug("intentExistCheck:INVOKED");
		if (intents != null) {
			for (Intent intent : intents) {
				languagesEnabled.add(intent.getLanguage().toLowerCase());
				if (intent.getIntentName().equalsIgnoreCase(newIntent.getIntentName())) {
					return true;
				}
			}
		}
		return false;
	}

	public List<Intent> getAllIntents() {
		logger.debug("getAllIntents:INVOKED");
		try {
			List<Intent> intents = getIntents();
			if (intents == null) {
				throw new IOException("Data/Files does not exists");
			}
			return intents;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private List<Intent> getIntents() throws IOException {
		logger.debug("getIntents:INVOKED");
		File file = new File(bot_json_file_path);
		List<Intent> intents = new ArrayList<>();
		try {
			if (file.exists()) {
				intents = objectMapper.readValue(file,
						objectMapper.getTypeFactory().constructCollectionType(List.class, Intent.class));

				for (Intent intent : intents) {
					languagesEnabled.add(intent.getLanguage().toLowerCase());
				}

				return intents;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return intents;
		}

		return intents;
	}

	public void saveIntents(List<Intent> intents) throws IOException {
		logger.debug("saveIntents:INVOKED");
		objectMapper.writeValue(new File(bot_json_file_path), intents);
	}

	public String deleteIntent(String intentName) {
		logger.debug("deleteIntent:INVOKED");
		String userResp = "";
		String intentLanguage = "";

		try {

			List<Intent> intents = getIntents();
			boolean found = false;

			for (int i = 0; i < intents.size(); i++) {
				if (intents.get(i).getIntentName().equalsIgnoreCase(intentName)) {
					intentLanguage = intents.get(i).getLanguage();
					intents.remove(i);
					found = true;
					break;
				}
			}

			if (!found) {
				userResp = "Intent with name '" + intentName + "' not found.";
				return userResp;
			}

			saveIntents(intents);

			File intentFile = new File(bot_intent_file_path + intentName + ".txt");
			if (intentFile.exists()) {
				boolean delete = intentFile.delete();
				if (delete) {
					logger.info("Intent file deleted: " + intentFile.getName());
					trainingTheBot(intentLanguage);
					return "Intent file deleted: " + intentFile.getName();
				} else {
					logger.info("Failed to delete intent file: " + intentFile.getName());
					return "Failed to delete intent file: " + intentFile.getName();
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
			userResp = "Error writing the files";
		}

		return userResp;

	}

	public String addEntites(EntityRequest entityRequest) {
		logger.debug("addEntites:INVOKED");
		String userResp = "";
		try {
			openNlpNerService.trainEntities(entityRequest);
			userResp = "Entities added or updated successfully.";
		} catch (Exception e) {
			e.printStackTrace();
			userResp = "Error added or updated entities.";
		}
		return userResp;
	}

	public EntityRequest getEntites() {
		logger.debug("getEntites:INVOKED");
		EntityRequest allEntities = null;
		try {
			allEntities = openNlpNerService.getAllEntities();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return allEntities;
	}

	private List<Response> predictedLanguageResponse(List<Response> intentIdentification, String languageString)
			throws Exception {
		logger.debug("predictedLanguageResponse:INVOKED");
		for (Response response : intentIdentification) {
			String responseText = response.getContent().getText();

			String detectLanguage = language.detectLanguage(responseText);
			String predictedLanguge = languageMapper.getLanguage(detectLanguage.toLowerCase());
			System.out.println("pridicted language is : " + predictedLanguge);

			languagesEnabled.contains(predictedLanguge.toLowerCase());
		}
		return intentIdentification;
	}

}
