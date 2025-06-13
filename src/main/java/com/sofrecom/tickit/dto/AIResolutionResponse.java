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
public class AIResolutionResponse {
    private UUID ticketId;
    private String suggestions;
    private int similarCasesFound;
    private double confidenceScore;
    private LocalDateTime timestamp;
    private List<String> actionItems;
}