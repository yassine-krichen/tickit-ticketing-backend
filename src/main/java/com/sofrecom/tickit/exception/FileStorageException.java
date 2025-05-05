package com.sofrecom.tickit.exception;

/**
 * Exception thrown when file storage operations fail.
 */
public class FileStorageException extends RuntimeException {

    /**
     * Creates a new FileStorageException with the given message.
     *
     * @param message The exception message
     */
    public FileStorageException(String message) {
        super(message);
    }

    /**
     * Creates a new FileStorageException with the given message and cause.
     *
     * @param message The exception message
     * @param cause The cause of the exception
     */
    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}