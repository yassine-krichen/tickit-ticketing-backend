package com.sofrecom.tickit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIAnalysisResponse {
    private String analysis;
    private LocalDateTime timestamp;
    private int dataPoints;
    private AnalysisStatus status;
    private String errorMessage;

    public enum AnalysisStatus {
        SUCCESS, PARTIAL, ERROR
    }
}
