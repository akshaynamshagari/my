package com.letmecall.rgt.chatbot.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letmecall.rgt.chatbot.model.ChatBot;
import com.letmecall.rgt.chatbot.servicesImpl.MessageService;
import com.letmecall.rgt.rest.helper.ChatBotServiceHelper;

@Service
public class DbMessageService implements MessageService {
	
	private static final Logger logger = LoggerFactory.getLogger(DbMessageService.class);

	@Autowired
    private ChatBotServiceHelper helperService;

    @Override
    public void saveMessage(ChatBot chatBot) {
		logger.debug("saveMessage:INVOKED");
		helperService.saveChatMessages(chatBot);
    }
}
