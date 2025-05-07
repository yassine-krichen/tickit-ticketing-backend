package com.sofrecom.tickit.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.sofrecom.tickit.model.ERole;
import com.sofrecom.tickit.model.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

/**
 * Request object for admin-level notification creation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Admin request to create notifications for multiple users by role")
public class NotificationAdminCreateRequest {
    
    @NotEmpty(message = "At least one role must be specified")
    @Schema(description = "Target roles for notification", required = true)
    private Set<ERole> roles;
    
    @NotNull(message = "Notification type is required")
    @Schema(description = "Type of notification", required = true)
    private NotificationType type;
    
    @NotNull(message = "Message is required")
    @Size(max = 500, message = "Message cannot exceed 500 characters")
    @Schema(description = "Human-readable notification message", required = true)
    private String message;
    
    @Schema(description = "JSON payload with additional context")
    private JsonNode payload;
}