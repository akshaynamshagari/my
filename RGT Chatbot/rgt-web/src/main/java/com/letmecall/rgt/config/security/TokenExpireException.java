package com.letmecall.rgt.config.security;

public class TokenExpireException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TokenExpireException(String message) {
		super(message);
	}

}
