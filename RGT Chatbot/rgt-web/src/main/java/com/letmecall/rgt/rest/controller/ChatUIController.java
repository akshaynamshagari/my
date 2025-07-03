package com.letmecall.rgt.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping(value = RgtResource.CHATBOT_BASE_URL)
public class ChatUIController {
	
	private static final Logger logger = LoggerFactory.getLogger(ChatUIController.class);
	
	@GetMapping(value = RgtResource.CHATBOT_UI_CHAT)
    public String home(HttpServletRequest request, Model model) {
		logger.debug("home:INVOKED");
		HttpSession session = request.getSession();
        String sessionId = session.getId();
        model.addAttribute("sessionId", sessionId);
        return "chatbot";
    }
}