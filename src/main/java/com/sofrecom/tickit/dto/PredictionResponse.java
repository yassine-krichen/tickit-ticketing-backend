package com.sofrecom.tickit.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictionResponse {
    private UUID treatmentActId;
    private String prediction;
    private double historicalBlockRate;
    private List<String> riskFactors;
    private int similarCasesAnalyzed;
    private LocalDateTime timestamp;
    private String riskLevel; // HIGH, MEDIUM, LOW
}