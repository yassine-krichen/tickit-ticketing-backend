package com.sofrecom.tickit.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.sofrecom.tickit.model.ERole;
import com.sofrecom.tickit.model.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Response object for Notification data.
 * Used for sending notification information to clients.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Notification information")
public class NotificationResponse {
    
    @Schema(description = "Unique identifier of the notification")
    private Long id;
    
    @Schema(description = "User ID to whom this notification belongs")
    private Long userId;
    
    @Schema(description = "Target roles for this notification")
    private Set<ERole> target;
    
    @Schema(description = "Human-readable notification message")
    private String message;
    
    @Schema(description = "Additional context data for the notification")
    private JsonNode payload;
    
    @Schema(description = "Whether the notification has been read")
    private Boolean readFlag;
    
    @Schema(description = "When the notification was created")
    private LocalDateTime createdAt;
    
    @Schema(description = "When the notification was marked as read (null if unread)")
    private LocalDateTime readAt;
    
    @Schema(description = "Type of notification")
    private NotificationType type;
}