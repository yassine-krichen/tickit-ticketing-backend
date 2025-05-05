package com.sofrecom.tickit.impl;

import com.sofrecom.tickit.config.StorageProperties;
import com.sofrecom.tickit.exception.FileStorageException;
import com.sofrecom.tickit.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Implementation of FileStorageService that stores files on the local disk.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DiskFileStorageServiceImpl implements FileStorageService {

    private final StorageProperties storageProperties;
    private Path rootLocation;

    /**
     * Initializes the storage directory.
     */
    @PostConstruct
    @Override
    public void init() {
        try {
            this.rootLocation = Paths.get(storageProperties.getRoot()).toAbsolutePath().normalize();
            Files.createDirectories(rootLocation);
            log.info("Initialized file storage at: {}", rootLocation);
        } catch (IOException e) {
            throw new FileStorageException("Could not initialize storage", e);
        }
    }

    /**
     * Stores a file on disk with a unique name in a date-based directory structure.
     *
     * @param file The file to store
     * @param activityId The ID of the activity this file belongs to
     * @return The relative path where the file is stored
     * @throws IOException If an I/O error occurs
     */
    @Override
    public String store(MultipartFile file, Long activityId) throws IOException {
        if (file.isEmpty()) {
            throw new FileStorageException("Failed to store empty file");
        }

        // Sanitize filename
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        
        if (originalFilename.contains("..")) {
            throw new FileStorageException("Filename contains invalid path sequence: " + originalFilename);
        }

        // Generate date-based directory structure
        LocalDate today = LocalDate.now();
        String datePath = today.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        
        // Create activity-specific directory with UUID prefix for filename
        String relativeDir = String.format("%s/activity_%d", datePath, activityId);
        String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;
        
        // Create the full storage path
        Path targetDirectory = this.rootLocation.resolve(relativeDir);
        Files.createDirectories(targetDirectory);
        
        Path targetPath = targetDirectory.resolve(uniqueFilename);
        
        // Copy the file to the target location
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
        
        // Return the relative path from the storage root
        return Paths.get(relativeDir, uniqueFilename).toString().replace('\\', '/');
    }

    /**
     * Loads a file from the given storage path.
     *
     * @param storagePath The relative storage path
     * @return The Path object for the file
     */
    @Override
    public Path load(String storagePath) {
        return rootLocation.resolve(storagePath);
    }
}