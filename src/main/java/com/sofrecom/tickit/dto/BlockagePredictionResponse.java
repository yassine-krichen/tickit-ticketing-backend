package com.sofrecom.tickit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlockagePredictionResponse {
    private RiskLevel riskLevel;
    private String explanation;
    private double riskScore; // 0-1 probability

    public enum RiskLevel {
        LOW,
        MEDIUM,
        HIGH
    }
}