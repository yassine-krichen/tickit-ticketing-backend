package com.sofrecom.tickit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class EntityCreationException extends RuntimeException {
    private final String entityName;

    public EntityCreationException(String entityName, String message) {
        super(String.format("Error creating %s: %s", entityName, message));
        this.entityName = entityName;
    }

    public EntityCreationException(String entityName, String message, Throwable cause) {
        super(String.format("Error creating %s: %s", entityName, message), cause);
        this.entityName = entityName;
    }

    public String getEntityName() {
        return entityName;
    }
}
