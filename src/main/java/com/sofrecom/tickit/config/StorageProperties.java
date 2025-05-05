package com.sofrecom.tickit.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for file storage.
 */
@Configuration
@ConfigurationProperties(prefix = "tickit.storage")
@Data
public class StorageProperties {

    /**
     * Root directory for storing attachments.
     * Can be absolute or relative path.
     */
    private String root = "uploads/attachments";
    
    /**
     * Maximum file size allowed in bytes.
     * Default is 10MB.
     */
    private long maxFileSize = 10 * 1024 * 1024;
    
    /**
     * Allowed file MIME types.
     * Default allows common document and image formats.
     */
    private String[] allowedTypes = {
        "image/jpeg", 
        "image/png", 
        "image/gif", 
        "application/pdf", 
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "text/plain"
    };
}