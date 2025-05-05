package com.sofrecom.tickit.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for activity comment responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object containing activity comment details")
public class TicketActivityCommentResponse {
    
    /**
     * Unique identifier for the comment.
     */
    @Schema(description = "Unique identifier for the comment", example = "1")
    private Long id;
    
    /**
     * ID of the activity this comment belongs to.
     */
    @Schema(description = "ID of the activity this comment belongs to", example = "42")
    private Long activityId;
    
    /**
     * Content of the comment.
     */
    @Schema(description = "Content of the comment", example = "This looks good, approved!")
    private String content;
    
    /**
     * Username of the person who wrote this comment.
     */
    @Schema(description = "Username of the person who wrote this comment", example = "john.doe")
    private String username;
    
    /**
     * When this comment was created.
     */
    @Schema(description = "Timestamp when this comment was created", example = "2025-05-02T10:30:00")
    private LocalDateTime createdAt;
    
    /**
     * When this comment was last updated.
     */
    @Schema(description = "Timestamp when this comment was last updated", example = "2025-05-02T11:15:00")
    private LocalDateTime updatedAt;
}