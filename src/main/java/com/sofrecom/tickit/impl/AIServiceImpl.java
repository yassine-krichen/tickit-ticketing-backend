package com.sofrecom.tickit.impl;

import com.sofrecom.tickit.dto.AIAnalysisResponse;
import com.sofrecom.tickit.dto.AIResolutionResponse;
import com.sofrecom.tickit.dto.ActeTraitementResponse;
import com.sofrecom.tickit.dto.BlockagePredictionResponse;
import com.sofrecom.tickit.model.LedgerEntry;
import com.sofrecom.tickit.model.Ticket;
import com.sofrecom.tickit.service.AIService;
import com.sofrecom.tickit.service.ActeTraitementService;
import com.sofrecom.tickit.service.LedgerService;
import com.sofrecom.tickit.service.TicketService;
import com.sofrecom.tickit.util.AIPromptBuilder;
import com.sofrecom.tickit.util.AIResponseParser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIServiceImpl implements AIService {
    
    @Value("${llm.endpoint}")
    private String llmEndpoint;
    
    @Value("${llm.model}")
    private String modelName;
    
    private final RestTemplate restTemplate;
    private final ActeTraitementService acteTraitementService;
    private final TicketService ticketService;
    private final LedgerService ledgerService;

    
    @Override
    public String queryWithContext(String prompt, String additionalContext) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, Object> body = new HashMap<>();
            body.put("model", modelName);
            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", buildEnhancedPrompt(prompt, additionalContext));
            body.put("messages", Collections.singletonList(message));
            body.put("temperature", 0.3);
            body.put("max_tokens", 1000);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(llmEndpoint, request, Map.class);
            return new AIResponseParser().extractAIResponse(response);
        } catch (Exception e) {
            log.error("Error querying AI service: {}", e.getMessage(), e);
            return "Error processing AI request";
        }
    }

    @Override
    public AIAnalysisResponse analyzeSystemStatistics() {
        log.info("Starting FTTH system performance analysis");
        
        try {
            // Get required data with null checks
            Map<String, Double> resolutionMetrics = ticketService.getResolutionMetrics();
            if (resolutionMetrics == null) {
                resolutionMetrics = new HashMap<>();
                log.warn("Resolution metrics returned null, using empty map");
            }

            Map<String, Object> trends = ticketService.getTicketTrends(30);
            if (trends == null) {
                trends = new HashMap<>();
                log.warn("Ticket trends returned null, using empty map");
            }
            
            // Make AI query with error handling
            String response = queryWithContext(
                AIPromptBuilder.buildSystemAnalysisPrompt(resolutionMetrics, trends),
                "Analyzing FTTH system performance metrics and trends"
            );
            
            if (response == null || response.equals("Error processing AI request")) {
                log.error("Failed to get valid AI response");
                throw new RuntimeException("AI service returned invalid response");
            }
            
            log.debug("Received AI analysis response: {}", response);
            
            // Parse response with validation
            AIResponseParser parser = new AIResponseParser();
            Map<String, Double> metrics = parser.extractPerformanceMetrics(response);
            List<AIAnalysisResponse.TrendInsight> trendInsights = parser.extractTrends(response);
            List<AIAnalysisResponse.RiskArea> risks = parser.extractRiskAreas(response);
            List<String> recs = parser.extractRecommendations(response);
            
            // Build response with validation
            return AIAnalysisResponse.builder()
                .performanceMetrics(metrics != null ? metrics : new HashMap<>())
                .trends(trendInsights != null ? trendInsights : new ArrayList<>())
                .riskAreas(risks != null ? risks : new ArrayList<>())
                .recommendations(recs != null ? recs : new ArrayList<>())
                .build();
                
        } catch (Exception e) {
            log.error("Error performing system analysis: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to complete system analysis: " + e.getMessage(), e);
        }
    }

    @Override
    public AIResolutionResponse suggestResolution(UUID ticketId) {
        log.info("Generating resolution suggestions for ticket {}", ticketId);
        
        try {
            Ticket ticket = ticketService.getTicketById(ticketId);
            List<Ticket> similarTickets = ticketService.findSimilarResolvedTickets(
                ticket.getSubject(), ticket.getType(), 5);
            List<LedgerEntry> caseHistory = ledgerService.getLedgerForCase(
                ticket.getActeTraitement().getAssociatedCase().getId().toString(), 
                "TREATMENT_ACT"
            );

            String response = queryWithContext(
                AIPromptBuilder.buildResolutionSuggestionPrompt(ticket, similarTickets, caseHistory),
                "Analyzing similar resolved tickets and case history and suggest solutions"
            );
            log.debug("Received AI resolution response for ticket {}: {}", ticketId, response);

            AIResponseParser parser = new AIResponseParser();
            String primarySolution = parser.extractPrimarySolution(response);
            
            // Log if primary solution is missing
            if (primarySolution == null || primarySolution.isEmpty()) {
                log.warn("No primary solution found for ticket {}", ticketId);
            }

            return AIResolutionResponse.builder()
                .primarySolution(primarySolution) // Add this line
                .alternativeSolutions(parser.extractAlternativeSolutions(response))
                .requiredSteps(parser.extractRequiredSteps(response))
                .potentialRisks(parser.extractPotentialRisks(response))
                .requiredResources(parser.extractRequiredResources(response))
                .confidenceScore(calculateConfidenceScore(similarTickets.size()))
                .estimatedEffort(parser.extractEstimatedEffort(response))
                .build();
        } catch (Exception e) {
            log.error("Error generating resolution for ticket {}: {}", ticketId, e.getMessage(), e);
            throw new RuntimeException("Failed to generate resolution suggestions", e);
        }
    }
    
    @Override
    public BlockagePredictionResponse predictBlockageRiskWithDetails(String treatmentActId) {
        try {
            log.info("Starting blockage prediction for treatment act {}", treatmentActId);
            
            ActeTraitementResponse act = acteTraitementService.getActeTraitementById(treatmentActId);
            List<LedgerEntry> similarActs = ledgerService.findSimilarTreatmentActs(
                act.getPrestationName(), 
                act.getPriorite(), 
                10
            );
            
            String response = queryWithContext(
                AIPromptBuilder.buildBlockagePredictionPrompt(act, similarActs),
                "Analyzing similar treatment acts for blockage prediction"
            );
            log.debug("Received AI prediction response: {}", response);
            
            AIResponseParser parser = new AIResponseParser();
            return parser.parseBlockagePrediction(response);
        } catch (Exception e) {
            log.error("Error predicting blockage for act {}: {}", treatmentActId, e.getMessage(), e);
            return new AIResponseParser().createDefaultPrediction();
        }
    }

    private String buildEnhancedPrompt(String originalPrompt, String context) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an AI analyst specializing in FTTH deployment ticketing systems. Focus on:\n")
              .append("- Fiber installation workflow analysis\n")
              .append("- Network deployment ticket patterns\n")
              .append("- Installation act resolution paths\n")
              .append("- SLA compliance predictions\n\n");
        
        if (context != null && !context.isEmpty()) {
            prompt.append("Context:\n").append(context).append("\n\n");
        }
        
        prompt.append("Query:\n").append(originalPrompt);
        return prompt.toString();
    }

    private double calculateConfidenceScore(int similarCaseCount) {
        if (similarCaseCount > 10) return 0.9;
        if (similarCaseCount > 5) return 0.8;
        if (similarCaseCount > 2) return 0.7;
        return 0.6;
    }
}