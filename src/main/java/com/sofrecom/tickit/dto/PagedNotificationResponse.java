package com.sofrecom.tickit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response object for paginated notifications.
 * Includes pagination metadata along with notification content.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Paginated notification response with metadata")
public class PagedNotificationResponse {
    
    @Schema(description = "List of notification items for the current page")
    private List<NotificationResponse> content;
    
    @Schema(description = "Total number of notifications matching the filter criteria")
    private long totalElements;
    
    @Schema(description = "Total number of pages available")
    private int totalPages;
    
    @Schema(description = "Number of items per page")
    private int size;
    
    @Schema(description = "Current page number (0-based)")
    private int number;
    
    @Schema(description = "Whether this is the first page")
    private boolean first;
    
    @Schema(description = "Whether this is the last page")
    private boolean last;
}