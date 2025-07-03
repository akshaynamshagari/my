package com.letmecall.rgt.rest.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 
 * @author subbareddy
 *
 */
public class UserAlreadyExistAuthenticationException extends AuthenticationException {
 
    private static final long serialVersionUID = 5570981880007077317L;
 
    public UserAlreadyExistAuthenticationException(final String msg) {
        super(msg);
    }
 
}