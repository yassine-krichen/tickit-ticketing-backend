package com.sofrecom.tickit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIResolutionResponse {
    private String primarySolution;
    private List<String> alternativeSolutions;
    private List<String> requiredSteps;
    private List<String> potentialRisks;
    private double confidenceScore;
    private EstimatedEffort estimatedEffort;
    private List<String> requiredResources;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EstimatedEffort {
        private int minHours;
        private int maxHours;
        private DifficultyLevel difficulty;
    }

    public enum DifficultyLevel {
        EASY, MODERATE, COMPLEX
    }
}
