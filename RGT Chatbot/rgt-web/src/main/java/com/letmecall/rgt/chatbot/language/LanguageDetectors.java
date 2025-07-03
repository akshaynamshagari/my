package com.letmecall.rgt.chatbot.language;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import opennlp.tools.langdetect.Language;
import opennlp.tools.langdetect.LanguageDetector;
import opennlp.tools.langdetect.LanguageDetectorME;
import opennlp.tools.langdetect.LanguageDetectorModel;

@Component
public class LanguageDetectors {
	
	private static final Logger logger = LoggerFactory.getLogger(LanguageDetectors.class);
	
	@Value("${chatbot.path.bot_model_language_detect_file_path}")
	private String bot_model_language_detect_file_path;
	
	public String detectLanguage(String input) {
		logger.debug("detectLanguage:INVOKED");
		InputStream is;
		String langString = "";
		try {
			is = new FileInputStream(bot_model_language_detect_file_path);
			LanguageDetectorModel langModel = new LanguageDetectorModel(is);
			LanguageDetector langDetector = new LanguageDetectorME(langModel);
			Language language = langDetector.predictLanguage(input);	
			langString =  language.getLang();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			langString = "File Not Found Error";
		}
		return langString;
	}
}
