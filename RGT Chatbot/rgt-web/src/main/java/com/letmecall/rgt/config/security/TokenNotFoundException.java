package com.letmecall.rgt.config.security;

public class TokenNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TokenNotFoundException(String message) {
		super(message);
	}

}