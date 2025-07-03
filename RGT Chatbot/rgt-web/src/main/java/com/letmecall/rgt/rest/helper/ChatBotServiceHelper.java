package com.letmecall.rgt.rest.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.letmecall.rgt.chatbot.model.ChatBot;
import com.letmecall.rgt.utility.ObjectCopier;
import com.letmecall.service.common.ChatBotService;

@Component
public class ChatBotServiceHelper {

	@Autowired
    private ChatBotService botService;
	
	public void saveChatMessages(ChatBot chatBot) {
		com.letmecall.rgt.notification.domain.ChatBot chatdata = new com.letmecall.rgt.notification.domain.ChatBot();
		ObjectCopier.copyObject(chatBot, chatdata);
		botService.saveChat(chatdata);
	}
}
