package com.sofrecom.tickit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when invalid sorting parameters (field or direction) are provided.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidSortParameterException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructs a new InvalidSortParameterException with the specified detail message.
     *
     * @param message the detail message
     */
    public InvalidSortParameterException(String message) {
        super(message);
    }
}