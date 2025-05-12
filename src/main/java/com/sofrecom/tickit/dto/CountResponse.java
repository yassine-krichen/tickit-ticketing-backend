package com.sofrecom.tickit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response object for count operations.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Count operation response")
public class CountResponse {
    
    @Schema(description = "Number of items counted")
    private long count;
}