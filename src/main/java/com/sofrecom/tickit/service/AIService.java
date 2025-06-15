package com.sofrecom.tickit.service;

import com.sofrecom.tickit.dto.AIAnalysisResponse;
import com.sofrecom.tickit.dto.AIResolutionResponse;
import com.sofrecom.tickit.dto.BlockagePredictionResponse;

import java.util.UUID;

public interface AIService {
    String query(String prompt);
    String queryWithContext(String prompt, String additionalContext);
    AIAnalysisResponse analyzeSystemStatistics();
    AIResolutionResponse suggestResolution(UUID ticketId);
    BlockagePredictionResponse predictBlockageRiskWithDetails(String treatmentActId);
}
