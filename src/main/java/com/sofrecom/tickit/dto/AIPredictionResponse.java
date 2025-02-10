package com.sofrecom.tickit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI prediction response")
public class AIPredictionResponse {

    @Schema(description = "Whether the treatment act is likely to be blocked")
    private boolean likelyBlocked;

    @Schema(description = "Explanation for the prediction")
    private String explanation;

    @Schema(description = "Confidence score of the prediction (0-1)")
    private double confidenceScore;
}
