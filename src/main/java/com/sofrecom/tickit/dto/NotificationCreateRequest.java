package com.sofrecom.tickit.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.sofrecom.tickit.model.ERole;
import com.sofrecom.tickit.model.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Request object for creating notifications.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a notification")
public class NotificationCreateRequest {
    
    @Schema(description = "User ID to notify (use either userId or roles)")
    private Long recipientUser;
    
    @Schema(description = "Roles to target (use either userId or roles)")
    private ERole recipientRole;
    
    @NotNull
    @Schema(description = "Type of notification", required = true)
    private NotificationType type;
    
    @NotNull
    @Size(max = 500)
    @Schema(description = "Human-readable notification message", required = true)
    private String message;
    
    @Schema(description = "JSON payload with details for UI actions")
    private JsonNode payload;
}