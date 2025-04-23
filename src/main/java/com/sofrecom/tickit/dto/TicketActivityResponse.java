package com.sofrecom.tickit.dto;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.UUID;

import com.sofrecom.tickit.model.TicketActivity.ActivityType;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for TicketActivity.
 * Represents the response structure for ticket activity details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object containing ticket activity details")
public class TicketActivityResponse {

    /**
     * Unique identifier for the activity.
     */
    @Schema(description = "Unique identifier for the activity", example = "1")
    private Long id;

    /**
     * Reference to the ticket this activity belongs to.
     */
    @Schema(description = "UUID of the ticket this activity belongs to", 
            example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID ticketId;

    /**
     * Username of the person who performed this activity.
     */
    @Schema(description = "Username of the person who performed this activity", 
            example = "john.doe")
    private String username;

    /**
     * Type of activity performed.
     */
    @Schema(description = "Type of activity performed", 
            example = "UPDATE", 
            allowableValues = {"OPEN", "CLOSE", "UPDATE", "EMAIL", "CALL", "RESEARCH", "ASSIGNMENT", "OTHER"})
    private ActivityType type;

    /**
     * Detailed description of the activity performed.
     */
    @Schema(description = "Detailed description of the activity performed", 
            example = "Updated ticket priority from LOW to HIGH")
    private String description;

    /**
     * When the activity was started.
     */
    @Schema(description = "Timestamp when the activity was started", 
            example = "2025-04-23T14:30:00")
    private LocalDateTime startTime;

    /**
     * When the activity was completed (null if still in progress).
     */
    @Schema(description = "Timestamp when the activity was completed (null if still in progress)", 
            example = "2025-04-23T15:45:00", 
            nullable = true)
    private LocalDateTime endTime;

    /**
     * Computed duration between start and end time.
     */
    @Schema(description = "Duration between start and end time", 
            example = "PT1H15M", 
            nullable = true)
    private Duration duration;

    /**
     * Timestamp when this record was created.
     */
    @Schema(description = "Timestamp when this record was created", 
            example = "2025-04-23T14:30:00")
    private LocalDateTime createdAt;

    /**
     * Timestamp when this record was last updated.
     */
    @Schema(description = "Timestamp when this record was last updated", 
            example = "2025-04-23T14:30:00")
    private LocalDateTime updatedAt;
}
