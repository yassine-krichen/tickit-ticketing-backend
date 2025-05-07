package com.sofrecom.tickit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Request object for bulk operations with notification IDs.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request for bulk operations on notifications")
public class BulkIdsRequest {
    
    @NotEmpty(message = "IDs list cannot be empty")
    @Size(max = 100, message = "Cannot process more than 100 IDs at once")
    @Schema(description = "List of notification IDs", required = true)
    private List<Long> ids;
}