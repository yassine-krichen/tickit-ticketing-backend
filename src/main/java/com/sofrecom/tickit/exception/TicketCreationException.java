package com.sofrecom.tickit.exception;

public class TicketCreationException extends RuntimeException {
    public TicketCreationException(String message) {
        super(message);
    }
             
    public TicketCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
