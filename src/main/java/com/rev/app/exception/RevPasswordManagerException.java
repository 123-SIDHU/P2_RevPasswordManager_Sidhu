package com.rev.app.exception;

public class RevPasswordManagerException extends RuntimeException {
    public RevPasswordManagerException(String message) {
        super(message);
    }

    public RevPasswordManagerException(String message, Throwable cause) {
        super(message, cause);
    }
}
