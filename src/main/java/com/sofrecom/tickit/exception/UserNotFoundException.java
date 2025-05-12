package com.sofrecom.tickit.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String cuid) {
        super("Collaborator with CUID '" + cuid + "' not found");
    }
}
