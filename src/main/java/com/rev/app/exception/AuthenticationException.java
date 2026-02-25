package com.rev.app.exception;

public class AuthenticationException extends RevPasswordManagerException {
    public AuthenticationException(String message) {
        super(message);
    }
}
