package com.letmecall.rgt.chatbot.model;

import java.util.List;

public class Intent {
	private String intentName;
	private List<String> patterns;
	private List<Response> responses;
	private String Language;

	public Intent() {
		super();
	}
	
	public String getIntentName() {
		return intentName;
	}

	public void setIntentName(String intentName) {
		this.intentName = intentName;
	}

	public Intent(List<String> patterns, List<Response> responses) {
		this.patterns = patterns;
		this.responses = responses;
	}

	public List<String> getPatterns() {
		return patterns;
	}

	public List<Response> getResponses() {
		return responses;
	}

	public void setPatterns(List<String> patterns) {
		this.patterns = patterns;
	}

	public void setResponses(List<Response> responses) {
		this.responses = responses;
	}

	public String getLanguage() {
		return Language;
	}

	public void setLanguage(String language) {
		Language = language;
	}
}