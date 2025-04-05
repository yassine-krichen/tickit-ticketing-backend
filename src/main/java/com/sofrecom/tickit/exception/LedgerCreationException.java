package com.sofrecom.tickit.exception;

public class LedgerCreationException extends RuntimeException {
    public LedgerCreationException(String message) {
        super(message);
    }
    
    public LedgerCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
