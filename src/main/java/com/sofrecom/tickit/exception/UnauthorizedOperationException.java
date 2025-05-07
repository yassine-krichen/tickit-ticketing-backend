package com.sofrecom.tickit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when an unauthorized operation is attempted.
 * This is used when a user tries to access or modify resources they don't have permission for.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizedOperationException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructs a new UnauthorizedOperationException with a default message.
     */
    public UnauthorizedOperationException() {
        super("You do not have permission to perform this operation");
    }
    
    /**
     * Constructs a new UnauthorizedOperationException with a custom message.
     *
     * @param message The detail message
     */
    public UnauthorizedOperationException(String message) {
        super(message);
    }
}