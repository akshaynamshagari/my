package com.letmecall.rgt.chatbot.model;

import java.util.List;

public class EntityRequest {
	private List<String> data;
	private List<String> type;
	private List<String> language;
	public List<String> getData() {
		return data;
	}
	public void setData(List<String> data) {
		this.data = data;
	}
	public List<String> getType() {
		return type;
	}
	public void setType(List<String> type) {
		this.type = type;
	}
	public List<String> getLanguage() {
		return language;
	}
	public void setLanguage(List<String> language) {
		this.language = language;
	}
}
