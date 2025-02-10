package com.sofrecom.tickit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIAnalysisResponse {
    private Map<String, Double> performanceMetrics;
    private List<TrendInsight> trends;
    private List<String> recommendations;
    private List<RiskArea> riskAreas;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendInsight {
        private String category;
        private String description;
        private double significance;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RiskArea {
        private String area;
        private String description;
        private RiskLevel riskLevel;
        private List<String> mitigationSteps;
    }

    public enum RiskLevel {
        LOW, MEDIUM, HIGH
    }
}
