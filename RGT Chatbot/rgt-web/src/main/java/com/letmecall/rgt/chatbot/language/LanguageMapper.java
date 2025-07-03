package com.letmecall.rgt.chatbot.language;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
public class LanguageMapper {

	private static final Logger logger = LoggerFactory.getLogger(LanguageMapper.class);

	@Value("${chatbot.path.bot_text_language_file_path}")
	private String bot_text_language_file_path;

	public String getLanguage(String langCode) throws IOException {
		logger.debug("getLanguage:INVOKED");

		HashMap<String, String> langMap = readToHashmap();

		return langMap.get(langCode);

	}

	public String getLanguageKey(String languageName) throws IOException {
		logger.debug("getLanguageKey:INVOKED");
		HashMap<String, String> langMap = readToHashmap();

		for (Map.Entry<String, String> entry : langMap.entrySet()) {
			if (entry.getValue().toLowerCase().equalsIgnoreCase(languageName.toLowerCase())) {
				return entry.getKey();
			}
		}
		return null;
	}

	public HashMap<String, String> readToHashmap() throws IOException {
		logger.debug("readToHashmap:INVOKED");
		HashMap<String, String> map = new HashMap<String, String>();
		BufferedReader in = new BufferedReader(new FileReader(bot_text_language_file_path));
		String line = "";

		while ((line = in.readLine()) != null) {
			String parts[] = line.split("\t");
			map.put(parts[0], parts[1]);
		}
		in.close();

		return map;

	}
}