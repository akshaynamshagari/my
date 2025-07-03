package com.letmecall.rgt.chatbot.model;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;

public class Request implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@NotBlank(message = "input should not be null")
	private String input;
	
	@NotBlank(message = "sessionId should not be null")
	private String sessionId;

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
}