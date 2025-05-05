package com.sofrecom.tickit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for attachment responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object containing attachment details")
public class AttachmentResponse {
    
    /**
     * Unique identifier for the attachment.
     */
    @Schema(description = "Unique identifier for the attachment", example = "1")
    private Long id;
    
    /**
     * ID of the activity this attachment belongs to.
     */
    @Schema(description = "ID of the activity this attachment belongs to", example = "42")
    private Long activityId;
    
    /**
     * Original filename provided by the user.
     */
    @Schema(description = "Original filename", example = "report.pdf")
    private String filename;
    
    /**
     * MIME type of the file.
     */
    @Schema(description = "MIME type of the file", example = "application/pdf")
    private String contentType;
    
    /**
     * Size of the file in bytes.
     */
    @Schema(description = "Size of the file in bytes", example = "1048576")
    private Long size;
    
    /**
     * When this attachment was created.
     */
    @Schema(description = "Timestamp when this attachment was created", example = "2025-05-02T10:30:00")
    private LocalDateTime createdAt;
    
    /**
     * When this attachment was last updated.
     */
    @Schema(description = "Timestamp when this attachment was last updated", example = "2025-05-02T11:15:00")
    private LocalDateTime updatedAt;
}