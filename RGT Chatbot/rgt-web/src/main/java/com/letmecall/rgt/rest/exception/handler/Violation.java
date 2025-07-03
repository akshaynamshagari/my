package com.letmecall.rgt.rest.exception.handler;

import java.io.Serializable;

public class Violation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final String fieldName;

	private final String message;

	public Violation(String fieldName, String message) {
		this.fieldName = fieldName;
		this.message = message;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getMessage() {
		return message;
	}
}
