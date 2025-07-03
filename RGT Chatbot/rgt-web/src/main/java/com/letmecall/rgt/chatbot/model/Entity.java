package com.letmecall.rgt.chatbot.model;

public class Entity {
	private String value;
	private String type;

	public Entity(String value, String type) {
		this.value = value;
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public String getType() {
		return type;
	}
}
