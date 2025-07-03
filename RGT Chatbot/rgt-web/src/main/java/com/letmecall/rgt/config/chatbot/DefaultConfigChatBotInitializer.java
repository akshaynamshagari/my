package com.letmecall.rgt.config.chatbot;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letmecall.rgt.chatbot.model.Intent;
import com.letmecall.rgt.chatbot.model.IntentRequest;
import com.letmecall.rgt.chatbot.services.IntentService;

@Component
public class DefaultConfigChatBotInitializer implements ApplicationRunner {

	private static final Logger logger = LoggerFactory.getLogger(DefaultConfigChatBotInitializer.class);
	
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	@Value("${chatbot.path.bot_intent_file_path}")
	private String bot_model_file_path;
	
	@Value("${chatbot.path.bot_default_json_file_path}")
	private String bot_default_json_file_path;
	
	private final IntentService intentService;

	@Autowired
	public DefaultConfigChatBotInitializer(IntentService intentService) {
		this.intentService = intentService;
	}

    @Override
    public void run(ApplicationArguments args) throws StreamReadException, DatabindException, IOException {
    	logger.debug("run:INVOKED");
    	ClassPathResource resource = new ClassPathResource(bot_default_json_file_path);
    	List<Intent> intent = objectMapper.readValue(resource.getInputStream(), new TypeReference<List<Intent>>() {});
    	String bot_intent_file_path = bot_model_file_path;
    	if (!folderHasFiles(bot_intent_file_path)) {
    		for (Intent intents : intent) {
    			createIfNotExists(intents);
			}
		};
    }

    private void createIfNotExists(Intent intent) {
    	logger.debug("createIfNotExists:INVOKED");
    	IntentRequest intentRequest = new IntentRequest();
    	intentRequest.setIntentName(intent.getIntentName());
    	intentRequest.setLanguage(intent.getLanguage());
    	intentRequest.setPatterns(intent.getPatterns());
    	intentRequest.setResponses(intent.getResponses());
    	intentService.addIntent(intentRequest);
    }
    
    private boolean folderHasFiles(String folderPath) {
    	logger.debug("folderHasFiles:INVOKED");
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        return folder.exists() && folder.isDirectory() && files != null && files.length > 0;
    }
}
