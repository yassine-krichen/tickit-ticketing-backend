package com.sofrecom.tickit.dto;

import com.sofrecom.tickit.model.ERole;
import com.sofrecom.tickit.model.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Request object for filtering notifications by various criteria.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Filter criteria for querying notifications")
public class NotificationFilterRequest {
    
    @Schema(description = "Filter by read/unread status")
    private Boolean readFlag;
    
    @Schema(description = "Filter by notification type")
    private NotificationType type;
    
    @Schema(description = "Filter by target role")
    private ERole role;
    
    @Schema(description = "Filter by notifications created after this time")
    private LocalDateTime createdAfter;
    
    @Schema(description = "Filter by notifications created before this time")
    private LocalDateTime createdBefore;
    
    @Schema(description = "Search text to match in notification message")
    private String searchText;
}