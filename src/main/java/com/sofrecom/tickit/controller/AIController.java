package com.sofrecom.tickit.controller;

import com.sofrecom.tickit.dto.*;
import com.sofrecom.tickit.model.*;
import com.sofrecom.tickit.service.*;
import com.sofrecom.tickit.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Optional;
import java.util.Arrays;
import java.util.Comparator;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "AI", description = "AI-powered analysis and predictions endpoints")
public class AIController {

    private final AIService aiService;
    private final TicketService ticketService;
    private final ActeTraitementService acteTraitementService;
    private final LedgerService ledgerService;
    private final TicketActivityService activityService;

    @PostMapping("/analyse")
    @Operation(summary = "Analyze ticketing system statistics", 
              description = "Performs comprehensive analysis of ticket statistics and trends")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Analysis completed successfully"),
        @ApiResponse(responseCode = "500", description = "Error processing analysis")
    })
    public ResponseEntity<AIAnalysisResponse> analyzeSystem() {
        try {
            log.debug("Starting system analysis");
            
            List<TicketCount> statusCounts = ticketService.getTicketCountByStatus();
            List<TicketCount> typeCounts = ticketService.getTicketCountByType();
            Map<String, Object> trendData = ticketService.getTicketTrends(30);
            Map<String, Double> resolutionMetrics = ticketService.getResolutionMetrics();

            String analysis = buildAnalysisPrompt(statusCounts, typeCounts, trendData, resolutionMetrics);
            
            AIAnalysisResponse response = AIAnalysisResponse.builder()
                .analysis(analysis)
                .timestamp(LocalDateTime.now())
                .dataPoints(statusCounts.size() + typeCounts.size())
                .build();

            log.debug("Analysis completed successfully");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error performing system analysis", e);
            throw new RuntimeException("Failed to analyze system data", e);
        }
    }

     @PostMapping("/suggest/{ticketId}/enhanced")
    @Operation(summary = "Enhanced ticket resolution suggestions with similar case analysis")
    public ResponseEntity<AIResolutionResponse> suggestResolutionEnhanced(
            @PathVariable UUID ticketId) {

        // Load ticket
        Ticket ticket = ticketService.getTicketById(ticketId);
        
        // Get activities
        Page<TicketActivityResponse> activities = activityService.getTimeline(
            ticketId, null, null, null, 0, 10, "startTime", "desc"
        );
        
        // Get enhanced context
        String enhancedContext = aiService.getEnhancedContext(
            ticket.getSubject(), 
            ticket.getType().name(), 
            ticket.getStatus().name()
        );

        StringBuilder prompt = new StringBuilder();
        prompt.append("TICKET ANALYSIS REQUEST:\n\n");
        
        prompt.append("Current Ticket:\n");
        prompt.append("- ID: ").append(ticket.getId()).append("\n");
        prompt.append("- Subject: ").append(ticket.getSubject()).append("\n");
        prompt.append("- Description: ").append(ticket.getDescription()).append("\n");
        prompt.append("- Status: ").append(ticket.getStatus()).append("\n");
        prompt.append("- Priority: ").append(ticket.getPriority()).append("\n");
        prompt.append("- Type: ").append(ticket.getType()).append("\n");
        prompt.append("- Age: ").append(calculateTicketAge(ticket.getCreatedAt())).append(" hours\n");

        prompt.append("\nRecent Activity Timeline:\n");
        activities.getContent().forEach(activity -> 
            prompt.append("- ").append(activity.getStartTime()).append(": ")
                  .append(activity.getType()).append(" - ")
                  .append(activity.getDescription()).append("\n"));

        prompt.append("\nProvide structured resolution guidance:\n");
        prompt.append("1. IMMEDIATE ACTIONS (next 1-2 hours)\n");
        prompt.append("2. SHORT TERM STEPS (next 24 hours)\n");
        prompt.append("3. ESCALATION CRITERIA\n");
        prompt.append("4. ESTIMATED RESOLUTION TIME\n");
        prompt.append("5. PREVENTIVE MEASURES\n");

        String suggestions = aiService.queryWithContext(prompt.toString(), enhancedContext);
        
        return ResponseEntity.ok(AIResolutionResponse.builder()
            .ticketId(ticketId)
            .suggestions(suggestions)
            .similarCasesFound(countSimilarCases(enhancedContext))
            .confidenceScore(calculateConfidenceScore(ticket, activities.getContent()))
            .timestamp(LocalDateTime.now())
            .build());
    }
    
    @PostMapping("/predict/{treatmentActId}")
    @Operation(summary = "Predict if a treatment act is likely to be blocked",
              description = "Analyzes historical data to predict potential blockage")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Prediction generated successfully"),
        @ApiResponse(responseCode = "404", description = "Treatment act not found"),
        @ApiResponse(responseCode = "500", description = "Error generating prediction")
    })
    public ResponseEntity<PredictionResponse> predictBlockage(
        @Parameter(description = "ID of the treatment act", required = true)
        @PathVariable @NotNull UUID treatmentActId) {
        
        try {
            log.debug("Starting blockage prediction for treatment act: {}", treatmentActId);
        
            // Get current treatment act
            ActeTraitementResponse act = Optional.ofNullable(
                acteTraitementService.getActeTraitementById(treatmentActId.toString()))
            .orElseThrow(() -> new ResourceNotFoundException("Treatment act not found", "id", treatmentActId));
            log.debug("Retrieved treatment act: {}", act);

            // Get ledger entries for the case
            List<LedgerEntry> caseEntries = ledgerService.getLedgerForCase(
                act.getCaseId().toString(), "TREATMENT_ACT");
            
            // Get similar acts from ledger
            List<LedgerEntry> similarLedgerEntries = ledgerService.findSimilarTreatmentActs(
                act.getType_element(), 
                act.getPriorite(), 
                20
            );

            String prediction = buildPredictionPrompt(act, caseEntries, similarLedgerEntries);
            double blockRate = calculateBlockRate(similarLedgerEntries);
        
            PredictionResponse response = PredictionResponse.builder()
                .treatmentActId(treatmentActId)
                .prediction(prediction)
                .historicalBlockRate(blockRate)
                .riskFactors(identifyRiskFactors(act, similarLedgerEntries))
                .similarCasesAnalyzed(similarLedgerEntries.size())
                .timestamp(LocalDateTime.now())
                .build();

            log.debug("Prediction completed for treatment act: {}", treatmentActId);
            return ResponseEntity.ok(response);
        
        } catch (Exception e) {
            log.error("Error generating prediction for treatment act: {}", treatmentActId, e);
            throw new RuntimeException("Failed to generate prediction", e);
        }
    }

    // Helper methods moved to service layer or made private

    private long calculateTicketAge(LocalDateTime createdAt) {
        return ChronoUnit.HOURS.between(createdAt, LocalDateTime.now());
    }

    private int countSimilarCases(String context) {
        
        return (int) Arrays.stream(context.split("\\R"))
            .filter(line -> line.contains("Similar Resolved Cases:"))
            .count();
    }
    
    private double calculateConfidenceScore(Ticket ticket, List<TicketActivityResponse> activities) {
        double score = 0.5; // Base score
        
        // Boost confidence based on available data
        if (!activities.isEmpty()) score += 0.2;
        if (ticket.getDescription() != null && ticket.getDescription().length() > 50) score += 0.1;
        if (ticket.getPriority() != null) score += 0.1;
        if (ticket.getType() != null) score += 0.1;
        
        return Math.min(score, 1.0);
    }

    private String buildAnalysisPrompt(List<TicketCount> statusCounts, 
                                     List<TicketCount> typeCounts,
                                     Map<String, Object> trendData,
                                     Map<String, Double> resolutionMetrics) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Analyze the ticketing system with the following comprehensive data:\n\n");

        // Current Status
        prompt.append("CURRENT STATUS:\n");
        statusCounts.forEach(count -> 
            prompt.append("- ").append(count.getCategory()).append(": ")
                  .append(count.getCount()).append("\n"));

        // Types Distribution
        prompt.append("\nTYPE DISTRIBUTION:\n");
        typeCounts.forEach(count -> 
            prompt.append("- ").append(count.getCategory()).append(": ")
                  .append(count.getCount()).append("\n"));

        // Trends
        prompt.append("\nTREND ANALYSIS (30-day comparison):\n");
        trendData.forEach((key, value) -> 
            prompt.append("- ").append(key).append(": ").append(value).append("\n"));

        // Performance Metrics
        prompt.append("\nPERFORMANCE METRICS:\n");
        resolutionMetrics.forEach((metric, value) -> 
            prompt.append("- ").append(metric).append(": ").append(value).append("\n"));

        prompt.append("\nProvide structured analysis including:\n");
        prompt.append("1. Performance Assessment (Good/Concerning/Critical)\n");
        prompt.append("2. Key Insights and Patterns\n");
        prompt.append("3. Priority Action Items\n");
        prompt.append("4. Predictive Insights\n");

        return aiService.queryWithContext(prompt.toString(), "");
    }

    private String buildPredictionPrompt(ActeTraitementResponse act,
                                   List<LedgerEntry> caseEntries,
                                   List<LedgerEntry> similarLedgerEntries) {
    StringBuilder prompt = new StringBuilder();
    prompt.append("BLOCKAGE PREDICTION ANALYSIS:\n\n");
    
    // Current act details
    prompt.append("Target Treatment Act:\n");
    prompt.append("- ID: ").append(act.getIdacte()).append("\n");
    prompt.append("- Type: ").append(act.getType_element()).append("\n");
    prompt.append("- Duration: ").append(act.getDuree()).append(" days\n");
    prompt.append("- Priority: ").append(act.getPriorite()).append("\n");
    prompt.append("- Case ID: ").append(act.getCaseId()).append("\n");

    // Case history analysis
    prompt.append("\nCase History Analysis:\n");
    Map<String, Long> actionCounts = caseEntries.stream()
        .collect(Collectors.groupingBy(
            entry -> entry.getActionType().name(),
            Collectors.counting()
        ));
    actionCounts.forEach((action, count) -> 
        prompt.append("- ").append(action).append(": ").append(count).append("\n"));

    // Similar cases analysis
    prompt.append("\nSimilar Cases Pattern Analysis:\n");
    long totalCases = similarLedgerEntries.size();
    long blockedCases = similarLedgerEntries.stream()
        .filter(entry -> {
            try {
                String status = entry.getDataSnapshot().get("status").asText();
                return "BLOCKED".equals(status);
            } catch (Exception e) {
                return false;
            }
        })
        .count();
    
    double blockRate = totalCases > 0 ? (double) blockedCases / totalCases * 100 : 0;
    
    prompt.append("- Total Similar Cases: ").append(totalCases).append("\n");
    prompt.append("- Blocked Cases: ").append(blockedCases).append("\n");
    prompt.append("- Historical Block Rate: ").append(String.format("%.1f%%", blockRate)).append("\n");

    // Timeline analysis
    prompt.append("\nCase Timeline Analysis:\n");
    caseEntries.stream()
        .sorted(Comparator.comparing(LedgerEntry::getTimestamp))
        .forEach(entry -> 
            prompt.append("- ")
                  .append(entry.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE))
                  .append(": ")
                  .append(entry.getActionType())
                  .append("\n")
        );

    // Analysis request
    prompt.append("\nProvide structured prediction:\n");
    prompt.append("1. BLOCKAGE PROBABILITY (High/Medium/Low)\n");
    prompt.append("2. KEY RISK FACTORS\n");
    prompt.append("3. RECOMMENDED PREVENTIVE ACTIONS\n");
    prompt.append("4. MONITORING CHECKPOINTS\n");

    return aiService.queryWithContext(prompt.toString(), "");
}

private double calculateBlockRate(List<LedgerEntry> similarEntries) {
    if (similarEntries == null || similarEntries.isEmpty()) {
        return 0.0;
    }
    
    long blockedCount = similarEntries.stream()
        .filter(entry -> {
            try {
                return "BLOCKED".equals(entry.getDataSnapshot().get("status").asText());
            } catch (Exception e) {
                log.warn("Could not extract status from ledger entry", e);
                return false;
            }
        })
        .count();
        
    return (double) blockedCount / similarEntries.size();
}

private List<String> identifyRiskFactors(ActeTraitementResponse act, List<LedgerEntry> entries) {
    List<String> risks = new ArrayList<>();
    if (act == null || entries == null) return risks;

    // Basic risk factors
    if (act.getDuree() > 30) risks.add("Long duration treatment");
    if ("HIGH".equals(act.getPriorite())) risks.add("High priority case");
    
    // Ledger-based risk factors
    long recentBlockages = entries.stream()
        .filter(entry -> {
            try {
                LocalDateTime timestamp = entry.getTimestamp();
                return "BLOCKED".equals(entry.getDataSnapshot().get("status").asText()) &&
                    timestamp.isAfter(LocalDateTime.now().minusMonths(1));
                } catch (Exception e) {
                return false;
            }
         })
            .count();
        
        if (recentBlockages > 0) risks.add("Recent similar blockages detected");
     if (entries.size() > 10) risks.add("High activity volume");
    
     return risks;
    }

}
