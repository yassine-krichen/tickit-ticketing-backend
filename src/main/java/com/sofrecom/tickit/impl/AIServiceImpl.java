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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public String query(String prompt) {
        return queryWithContext(prompt, "");
    }
    
    @Override
    public String queryWithContext(String prompt, String additionalContext) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            String enhancedPrompt = buildEnhancedPrompt(prompt, additionalContext);
            
            Map<String, Object> body = new HashMap<>();
            body.put("model", modelName);
            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", enhancedPrompt);
            body.put("messages", Collections.singletonList(message));
            body.put("temperature", 0.3);
            body.put("max_tokens", 1000);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(llmEndpoint, request, Map.class);
            return extractAIResponse(response);
        } catch (Exception e) {
            log.error("Error querying AI service: {}", e.getMessage(), e);
            return "Error processing AI request";
        }
    }

    @Override
public AIAnalysisResponse analyzeSystemStatistics() {
    log.info("Starting FTTH system performance analysis");
    
    // Gather actual metrics
    Map<String, Double> resolutionMetrics = ticketService.getResolutionMetrics();
    Map<String, Object> trends = ticketService.getTicketTrends(30); // Last 30 days
    
    String prompt = String.format(
        "Analyze FTTH deployment system performance with these metrics:\n\n" +
        "Resolution Times:\n" +
        "- Average: %.2f hours\n" +
        "- Fastest: %.2f hours\n" +
        "- Slowest: %.2f hours\n\n" +
        "Volume Trends (30 days):\n" +
        "- Total Tickets: %d\n" +
        "Daily Pattern: %s\n\n" +
        "Provide analysis in sections:\n" +
        "1. Performance Metrics (resolution times, throughput)\n" +
        "2. Trends (volume patterns, peak times)\n" +
        "3. Recommendations (process improvements)\n" +
        "4. Risk Areas (SLA compliance, resource allocation)",
        resolutionMetrics.get("averageResolutionHours"),
        resolutionMetrics.get("fastestResolutionHours"),
        resolutionMetrics.get("slowestResolutionHours"),
        trends.get("totalTickets"),
        trends.get("dailyData").toString()
    );

    String response = query(prompt);
    log.debug("Received AI analysis response: {}", response);
    
    return AIAnalysisResponse.builder()
        .performanceMetrics(extractPerformanceMetrics(response))
        .trends(extractTrends(response))
        .recommendations(extractRecommendations(response))
        .riskAreas(extractRiskAreas(response))
        .build();
}

    @Override
public AIResolutionResponse suggestResolution(UUID ticketId) {
    log.info("Generating resolution suggestions for ticket {}", ticketId);
    
    Ticket ticket = ticketService.getTicketById(ticketId);
    List<Ticket> similarTickets = ticketService.findSimilarResolvedTickets(
        ticket.getSubject(), ticket.getType(), 5);
    List<LedgerEntry> caseHistory = ledgerService.getLedgerForCase(
        ticket.getActeTraitement().getAssociatedCase().getId().toString(), 
        "TREATMENT_ACT"
    );

    String prompt = String.format(
        "Analyze this FTTH ticket and suggest resolution:\n\n" +
        "Current Ticket:\n" +
        "- ID: %s\n" +
        "- Type: %s\n" +
        "- Subject: %s\n" +
        "- Status: %s\n\n" +
        "Similar Resolved Tickets:\n%s\n\n" +
        "Case History:\n%s\n\n" +
        "Provide structured resolution:\n" +
        "1. Primary Solution (based on similar tickets)\n" +
        "2. Alternative Solutions\n" +
        "3. Required Steps (ordered list)\n" +
        "4. Potential Risks\n" +
        "5. Required Resources",
        ticket.getId(),
        ticket.getType(),
        ticket.getSubject(),
        ticket.getStatus(),
        formatSimilarTickets(similarTickets),
        formatCaseHistory(caseHistory)
    );

    String response = query(prompt);
    log.debug("Received AI resolution response: {}", response);
    
    return AIResolutionResponse.builder()
        .primarySolution(extractPrimarySolution(response))
        .alternativeSolutions(extractAlternativeSolutions(response))
        .requiredSteps(extractRequiredSteps(response))
        .potentialRisks(extractPotentialRisks(response))
        .requiredResources(extractRequiredResources(response))
        .confidenceScore(calculateConfidenceScore(similarTickets.size()))
        .estimatedEffort(extractEstimatedEffort(response))
        .build();
}

private String formatSimilarTickets(List<Ticket> tickets) {
    StringBuilder sb = new StringBuilder();
    for (Ticket t : tickets) {
        sb.append(String.format("- %s: %s (Resolved in %d hours)\n",
            t.getId(),
            t.getSubject(),
            Duration.between(t.getCreatedAt(), t.getResolvedAt()).toHours()
        ));
    }
    return sb.toString();
}

private String formatCaseHistory(List<LedgerEntry> entries) {
    StringBuilder sb = new StringBuilder();
    for (LedgerEntry entry : entries) {
        sb.append(String.format("- %s: %s\n",
            entry.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            entry.getActionType()
        ));
    }
    return sb.toString();
}
    
    @Override
    public BlockagePredictionResponse predictBlockageRiskWithDetails(String treatmentActId) {
        try {
            log.info("Starting blockage prediction for treatment act {}", treatmentActId);
            
            ActeTraitementResponse act = acteTraitementService.getActeTraitementById(treatmentActId);
            String prompt = buildBlockagePredictionPrompt(act);
            String response = query(prompt);
            
            log.debug("Received AI prediction response: {}", response);
            
            return parseBlockagePrediction(response);
        } catch (Exception e) {
            log.error("Error predicting blockage for act {}: {}", treatmentActId, e.getMessage(), e);
            return createDefaultPrediction();
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

    private String extractAIResponse(Map<String, Object> response) {
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            Map<String, Object> firstChoice = choices.get(0);
            Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
            return (String) message.get("content");
        } catch (Exception e) {
            log.error("Error extracting AI response: {}", e.getMessage());
            return "";
        }
    }

    private Map<String, Double> extractPerformanceMetrics(String response) {
        Map<String, Double> metrics = new HashMap<>();
        
        String metricsSection = extractSection(response, "Performance Metrics");
        
        if (!metricsSection.isEmpty()) {
            // Pattern to match "Metric Name: Value%" or "Metric Name: Value unit"
            Pattern metricPattern = Pattern.compile("([a-zA-Z\\s]+):\\s*([0-9.]+)\\s*[%a-zA-Z]*", Pattern.CASE_INSENSITIVE);
            Matcher matcher = metricPattern.matcher(metricsSection);
            
            while (matcher.find()) {
                String metricName = matcher.group(1).trim();
                try {
                    Double value = Double.parseDouble(matcher.group(2));
                    metrics.put(metricName, value);
                } catch (NumberFormatException e) {
                    log.debug("Could not parse metric value for {}: {}", metricName, matcher.group(2));
                }
            }
        }
        
        // Ensure we have basic FTTH metrics
        if (metrics.isEmpty()) {
            metrics.put("Average Resolution Time", 24.0);
            metrics.put("SLA Compliance Rate", 95.0);
            metrics.put("Treatment Success Rate", 85.0);
        }
        
        return metrics;
    }

    private List<AIAnalysisResponse.TrendInsight> extractTrends(String response) {
        List<AIAnalysisResponse.TrendInsight> trends = new ArrayList<>();
        String trendsSection = extractSection(response, "Trends");
        
        if (!trendsSection.isEmpty()) {
            String[] lines = trendsSection.split("\n");
            for (String line : lines) {
                line = line.trim();
                if (line.startsWith("-") || line.matches("^\\d+\\..*")) {
                    line = line.replaceAll("^[-*•\\d+\\.\\)\\s]+", "").trim();
                    AIAnalysisResponse.TrendInsight trend = parseTrendLine(line);
                    if (trend != null) {
                        trends.add(trend);
                    }
                }
            }
        }
        
        // Default FTTH-specific trends if none found
        if (trends.isEmpty()) {
            trends.add(AIAnalysisResponse.TrendInsight.builder()
                .category("Installation Volume")
                .description("Current FTTH installation request patterns")
                .significance(0.8)
                .build());
        }
        
        return trends;
    }

    private AIAnalysisResponse.TrendInsight parseTrendLine(String line) {
        // Pattern: "Category: Network | Description: Increasing traffic | Significance: 0.8"
        Pattern trendPattern = Pattern.compile("Category:\\s*([^|]+)\\s*\\|\\s*Description:\\s*([^|]+)\\s*\\|\\s*Significance:\\s*([0-9.]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = trendPattern.matcher(line);
        
        if (matcher.find()) {
            try {
                return AIAnalysisResponse.TrendInsight.builder()
                    .category(matcher.group(1).trim())
                    .description(matcher.group(2).trim())
                    .significance(Double.parseDouble(matcher.group(3)))
                    .build();
            } catch (NumberFormatException e) {
                log.debug("Could not parse significance value: {}", matcher.group(3));
            }
        }
        
        // Fallback: treat entire line as description with default values
        if (line.length() > 10) {
            return AIAnalysisResponse.TrendInsight.builder()
                .category("General")
                .description(line)
                .significance(0.5)
                .build();
        }
        
        return null;
    }

    private List<String> extractRecommendations(String response) {
        return extractListFromSection(response, "Recommendations");
    }

    private List<AIAnalysisResponse.RiskArea> extractRiskAreas(String response) {
        List<AIAnalysisResponse.RiskArea> riskAreas = new ArrayList<>();
        String riskSection = extractSection(response, "Risk Areas");
        
        if (!riskSection.isEmpty()) {
            String[] lines = riskSection.split("\n");
            for (String line : lines) {
                line = line.trim();
                if (line.startsWith("-") || line.matches("^\\d+\\..*")) {
                    line = line.replaceAll("^[-*•\\d+\\.\\)\\s]+", "").trim();
                    
                    AIAnalysisResponse.RiskArea riskArea = parseRiskAreaLine(line);
                    if (riskArea != null) {
                        riskAreas.add(riskArea);
                    }
                }
            }
        }
        
        // Default FTTH-specific risk if none found
        if (riskAreas.isEmpty()) {
            riskAreas.add(AIAnalysisResponse.RiskArea.builder()
                .area("SLA Compliance")
                .description("Risk of missing installation deadlines")
                .riskLevel(AIAnalysisResponse.RiskLevel.MEDIUM)
                .mitigationSteps(Arrays.asList(
                    "Review resource allocation",
                    "Prioritize approaching deadlines"
                ))
                .build());
        }
        
        return riskAreas;
    }

    private AIAnalysisResponse.RiskArea parseRiskAreaLine(String line) {
        // Pattern: "Area: Network | Description: High traffic | Risk: HIGH | Mitigation: Add bandwidth, QoS"
        Pattern riskPattern = Pattern.compile("Area:\\s*([^|]+)\\s*\\|\\s*Description:\\s*([^|]+)\\s*\\|\\s*Risk:\\s*([^|]+)\\s*\\|\\s*Mitigation:\\s*(.+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = riskPattern.matcher(line);
        
        if (matcher.find()) {
            AIAnalysisResponse.RiskLevel riskLevel = parseRiskLevel(matcher.group(3).trim());
            List<String> mitigationSteps = Arrays.asList(matcher.group(4).split(","));
            // Clean up mitigation steps
            mitigationSteps = mitigationSteps.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(ArrayList::new, (list, item) -> list.add(item), ArrayList::addAll);
            
            return AIAnalysisResponse.RiskArea.builder()
                .area(matcher.group(1).trim())
                .description(matcher.group(2).trim())
                .riskLevel(riskLevel)
                .mitigationSteps(mitigationSteps)
                .build();
        }
        
        // Fallback: treat as simple risk area
        if (line.length() > 10) {
            return AIAnalysisResponse.RiskArea.builder()
                .area("General Risk")
                .description(line)
                .riskLevel(AIAnalysisResponse.RiskLevel.LOW)
                .mitigationSteps(Arrays.asList("Monitor situation", "Take preventive action"))
                .build();
        }
        
        return null;
    }

    private AIAnalysisResponse.RiskLevel parseRiskLevel(String riskText) {
        String lower = riskText.toLowerCase();
        if (lower.contains("high")) {
            return AIAnalysisResponse.RiskLevel.HIGH;
        } else if (lower.contains("medium")) {
            return AIAnalysisResponse.RiskLevel.MEDIUM;
        } else {
            return AIAnalysisResponse.RiskLevel.LOW;
        }
    }

    private String extractPrimarySolution(String response) {
        String section = extractSection(response, "Primary Solution");
        return section.isEmpty() ? "No primary solution identified" : section.trim();
    }

    private List<String> extractAlternativeSolutions(String response) {
        List<String> alternatives = extractListFromSection(response, "Alternative Solutions");
        return alternatives.isEmpty() ? Arrays.asList(
            "Escalate to field operations team",
            "Schedule emergency intervention"
        ) : alternatives;
    }

    private List<String> extractRequiredSteps(String response) {
        List<String> steps = extractListFromSection(response, "Required Steps");
        return steps.isEmpty() ? Arrays.asList(
            "Verify installation parameters",
            "Check physical infrastructure",
            "Test optical signal"
        ) : steps;
    }

    private List<String> extractPotentialRisks(String response) {
        List<String> risks = extractListFromSection(response, "Potential Risks");
        return risks.isEmpty() ? Arrays.asList(
            "Installation delay",
            "Signal quality issues"
        ) : risks;
    }

    private List<String> extractRequiredResources(String response) {
        List<String> resources = extractListFromSection(response, "Required Resources");
        return resources.isEmpty() ? Arrays.asList(
            "FTTH technician",
            "Optical testing equipment"
        ) : resources;
    }

    private double calculateConfidenceScore(int similarCaseCount) {
        // Base confidence on number of similar cases found
        if (similarCaseCount > 10) return 0.9;
        if (similarCaseCount > 5) return 0.8;
        if (similarCaseCount > 2) return 0.7;
        return 0.6;
    }

    private AIResolutionResponse.EstimatedEffort extractEstimatedEffort(String response) {
        // Look for effort indicators in the response
        String lowerResponse = response.toLowerCase();
        
        int minHours = 2;
        int maxHours = 4;
        AIResolutionResponse.DifficultyLevel difficulty = AIResolutionResponse.DifficultyLevel.MODERATE;
        
        if (lowerResponse.contains("complex") || lowerResponse.contains("difficult") || lowerResponse.contains("challenging")) {
            difficulty = AIResolutionResponse.DifficultyLevel.COMPLEX;
            minHours = 4;
            maxHours = 8;
        } else if (lowerResponse.contains("simple") || lowerResponse.contains("easy") || lowerResponse.contains("quick")) {
            difficulty = AIResolutionResponse.DifficultyLevel.EASY;
            minHours = 1;
            maxHours = 2;
        }
        
        return AIResolutionResponse.EstimatedEffort.builder()
            .minHours(minHours)
            .maxHours(maxHours)
            .difficulty(difficulty)
            .build();
    }

    private String extractSection(String text, String sectionName) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        
        // Try different header formats
        String[] headerPatterns = {
            "**" + sectionName + ":**",
            "**" + sectionName + "**",
            sectionName + ":",
            "## " + sectionName,
            "# " + sectionName
        };
        
        int startIndex = -1;
        String usedPattern = null;
        
        for (String pattern : headerPatterns) {
            startIndex = text.indexOf(pattern);
            if (startIndex != -1) {
                startIndex += pattern.length();
                usedPattern = pattern;
                break;
            }
        }
        
        if (startIndex == -1) {
            return "";
        }
        
        // Find the next section or end of text
        int endIndex = text.length();
        for (String pattern : headerPatterns) {
            if (!pattern.equals(usedPattern)) {
                int nextSectionIndex = text.indexOf(pattern, startIndex);
                if (nextSectionIndex != -1 && nextSectionIndex < endIndex) {
                    endIndex = nextSectionIndex;
                }
            }
        }
        
        return text.substring(startIndex, endIndex).trim();
    }

    private List<String> extractListFromSection(String response, String sectionName) {
        List<String> items = new ArrayList<>();
        String section = extractSection(response, sectionName);
        
        if (!section.isEmpty()) {
            String[] lines = section.split("\n");
            for (String line : lines) {
                line = line.trim();
                if (!line.isEmpty()) {
                    // Remove bullet points, numbers, and formatting
                    line = line.replaceAll("^[-*•\\d+\\.\\)\\s]+", "").trim();
                    if (!line.isEmpty() && line.length() > 3) {
                        items.add(line);
                    }
                }
            }
        }
        
        return items;
    }

    private String buildBlockagePredictionPrompt(ActeTraitementResponse act) {
        List<LedgerEntry> similarActs = ledgerService.findSimilarTreatmentActs(
            act.getPrestationName(), 
            act.getPriorite(), 
            10
        );
    
        return String.format(
            "Analyze this FTTH treatment act for blockage risks:\n\n" +
            "Treatment Act:\n" +
            "- ID: %s\n" +
            "- Type: %s\n" +
            "- Status: %s\n" +
            "- Priority: %s\n\n" +
            "Similar Acts History:\n%s\n\n" +
            "Provide structured risk assessment:\n" +
            "1. Risk Level (LOW/MEDIUM/HIGH)\n" +
            "2. Detailed Explanation (max 500 chars)\n" +
            "3. Risk Score (0-100)\n" +
            "4. Key Risk Factors",
            act.getIdacte(),
            act.getPrestationName(),
            act.getStatus(),
            act.getPriorite(),
            formatSimilarActs(similarActs)
        );
    }
    
    private String formatSimilarActs(List<LedgerEntry> acts) {
        StringBuilder sb = new StringBuilder();
        for (LedgerEntry act : acts) {
            sb.append(String.format("- %s: %s\n",
                act.getEntityId(),
                act.getActionType()
            ));
        }
        return sb.toString();
    }

    private BlockagePredictionResponse parseBlockagePrediction(String response) {
        String lowerResponse = response.toLowerCase();
        BlockagePredictionResponse.RiskLevel riskLevel;
        double riskScore = 0.0;

        // Extract risk level
        if (lowerResponse.contains("high")) {
            riskLevel = BlockagePredictionResponse.RiskLevel.HIGH;
            riskScore = 0.8;
        } else if (lowerResponse.contains("medium")) {
            riskLevel = BlockagePredictionResponse.RiskLevel.MEDIUM;  
            riskScore = 0.5;
        } else {
            riskLevel = BlockagePredictionResponse.RiskLevel.LOW;
            riskScore = 0.2;
        }

        // Extract numerical risk score if present
        Pattern scorePattern = Pattern.compile("(?:risk\\s+score|score):\\s*([0-9.]+)", Pattern.CASE_INSENSITIVE);
        Matcher scoreMatcher = scorePattern.matcher(response);
        if (scoreMatcher.find()) {
            try {
                double extractedScore = Double.parseDouble(scoreMatcher.group(1));
                // Convert to 0-1 scale if needed
                riskScore = extractedScore > 1.0 ? extractedScore / 100.0 : extractedScore;
            } catch (NumberFormatException e) {
                log.debug("Could not parse risk score: {}", scoreMatcher.group(1));
            }
        }

        String explanation = extractExplanation(response);

        return BlockagePredictionResponse.builder()
            .riskLevel(riskLevel)
            .explanation(explanation)
            .riskScore(riskScore)
            .build();
    }

    private String extractExplanation(String response) {
        String explanation = extractSection(response, "Explanation");
        
        if (explanation.isEmpty()) {
            // Look for explanation patterns
            Pattern explanationPattern = Pattern.compile("explanation:\\s*(.+?)(?:\\n\\n|$)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
            Matcher matcher = explanationPattern.matcher(response);
            if (matcher.find()) {
                explanation = matcher.group(1).trim();
            }
        }
        
        if (explanation.isEmpty()) {
            // Fallback: take first substantial paragraph
            String[] paragraphs = response.split("\n\n");
            for (String paragraph : paragraphs) {
                paragraph = paragraph.trim();
                if (paragraph.length() > 50 && !paragraph.toLowerCase().contains("risk level")) {
                    explanation = paragraph;
                    break;
                }
            }
        }
        
        return explanation.isEmpty() ? "Risk assessment completed based on treatment parameters" : explanation;
    }

    private BlockagePredictionResponse createDefaultPrediction() {
        return BlockagePredictionResponse.builder()
            .riskLevel(BlockagePredictionResponse.RiskLevel.LOW)
            .explanation("Unable to analyze risk due to system error - defaulting to low risk")
            .riskScore(0.1)
            .build();
    }
}