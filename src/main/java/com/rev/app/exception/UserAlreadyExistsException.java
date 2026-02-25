package com.rev.app.exception;

public class UserAlreadyExistsException extends RevPasswordManagerException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
