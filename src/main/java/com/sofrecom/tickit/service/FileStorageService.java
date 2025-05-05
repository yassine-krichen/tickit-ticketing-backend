package com.sofrecom.tickit.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Service interface for file storage operations.
 * Abstracts the storage mechanism to support different implementations
 * (local disk, cloud storage, etc.)
 */
public interface FileStorageService {

    /**
     * Stores a file and returns the relative storage path.
     *
     * @param file The file to store
     * @param activityId The ID of the activity this file belongs to
     * @return The relative path where the file is stored
     * @throws IOException If an I/O error occurs
     */
    String store(MultipartFile file, Long activityId) throws IOException;

    /**
     * Loads a file from the given storage path.
     *
     * @param storagePath The relative storage path
     * @return The Path object for the file
     */
    Path load(String storagePath);

    /**
     * Initializes the storage (creates directories if needed).
     */
    void init();
}