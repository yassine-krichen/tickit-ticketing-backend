package com.sofrecom.tickit.util;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sofrecom.tickit.dto.AIAnalysisResponse;
import com.sofrecom.tickit.dto.AIAnalysisResponse.RiskArea;
import com.sofrecom.tickit.dto.AIAnalysisResponse.RiskLevel;
import com.sofrecom.tickit.dto.AIAnalysisResponse.TrendInsight;
import com.sofrecom.tickit.dto.AIResolutionResponse;
import com.sofrecom.tickit.dto.AIResolutionResponse.DifficultyLevel;
import com.sofrecom.tickit.dto.AIResolutionResponse.EstimatedEffort;
import com.sofrecom.tickit.dto.BlockagePredictionResponse;

@Slf4j
public class AIResponseParser {

    public String extractAIResponse(Map<String, Object> response) {
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

    public Map<String, Double> extractPerformanceMetrics(String response) {
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

    public List<AIAnalysisResponse.TrendInsight> extractTrends(String response) {
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

    public AIAnalysisResponse.TrendInsight parseTrendLine(String line) {
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

    public List<AIAnalysisResponse.RiskArea> extractRiskAreas(String response) {
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

    public AIAnalysisResponse.RiskArea parseRiskAreaLine(String line) {
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

    public AIAnalysisResponse.RiskLevel parseRiskLevel(String riskText) {
        String lower = riskText.toLowerCase();
        if (lower.contains("high")) {
            return AIAnalysisResponse.RiskLevel.HIGH;
        } else if (lower.contains("medium")) {
            return AIAnalysisResponse.RiskLevel.MEDIUM;
        } else {
            return AIAnalysisResponse.RiskLevel.LOW;
        }
    }


    public List<String> extractAlternativeSolutions(String response) {
        List<String> alternatives = extractListFromSection(response, "Alternative Solutions");
        return alternatives.isEmpty() ? Arrays.asList(
            "Escalate to field operations team",
            "Schedule emergency intervention"
        ) : alternatives;
    }

    public List<String> extractRequiredSteps(String response) {
        List<String> steps = extractListFromSection(response, "Required Steps");
        return steps.isEmpty() ? Arrays.asList(
            "Verify installation parameters",
            "Check physical infrastructure",
            "Test optical signal"
        ) : steps;
    }

    public List<String> extractPotentialRisks(String response) {
        List<String> risks = extractListFromSection(response, "Potential Risks");
        return risks.isEmpty() ? Arrays.asList(
            "Installation delay",
            "Signal quality issues"
        ) : risks;
    }

    public List<String> extractRequiredResources(String response) {
        List<String> resources = extractListFromSection(response, "Required Resources");
        return resources.isEmpty() ? Arrays.asList(
            "FTTH technician",
            "Optical testing equipment"
        ) : resources;
    }

    public double calculateConfidenceScore(int similarCaseCount) {
        // Base confidence on number of similar cases found
        if (similarCaseCount > 10) return 0.9;
        if (similarCaseCount > 5) return 0.8;
        if (similarCaseCount > 2) return 0.7;
        return 0.6;
    }

    public AIResolutionResponse.EstimatedEffort extractEstimatedEffort(String response) {
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

    public String extractSection(String text, String sectionName) {
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

    public List<String> extractListFromSection(String response, String sectionName) {
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

    
    public BlockagePredictionResponse parseBlockagePrediction(String response) {
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

    public String extractExplanation(String response) {
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

    public BlockagePredictionResponse createDefaultPrediction() {
        return BlockagePredictionResponse.builder()
            .riskLevel(BlockagePredictionResponse.RiskLevel.LOW)
            .explanation("Unable to analyze risk due to system error - defaulting to low risk")
            .riskScore(0.1)
            .build();
    }

    public List<String> extractRecommendations(String response) {
        List<String> recommendations = new ArrayList<>();
        String recommendationsSection = extractSection(response, "Recommendations");
        
        if (!recommendationsSection.isEmpty()) {
            String[] lines = recommendationsSection.split("\n");
            boolean hasStartedRiskSection = false;
            
            for (String line : lines) {
                line = line.trim();
                
                // Skip empty lines
                if (line.isEmpty()) {
                    continue;
                }
                
                // Check if we've hit the Risk Areas section
                if (line.toLowerCase().contains("risk area")) {
                    hasStartedRiskSection = true;
                    continue;
                }
                
                // Skip if we're in the risk section
                if (hasStartedRiskSection) {
                    continue;
                }
                
                // Process only bullet points or numbered items
                if (line.startsWith("-") || line.matches("^\\d+\\..*")) {
                    line = line.replaceAll("^[-*•\\d+\\.\\)\\s]+", "").trim();
                    
                    // Convert "Action Required:" format to "ATT INFO:"
                    if (line.startsWith("Action Required:")) {
                        line = line.replace("Action Required:", "ATT INFO:");
                    }
                    
                    // Format and add recommendation
                    String formattedRec = formatRecommendation(line);
                    if (formattedRec != null && !formattedRec.toLowerCase().contains("risk area")) {
                        recommendations.add(formattedRec);
                    }
                }
            }
        }
        
        // Provide default FTTH-specific recommendations if none found
        if (recommendations.isEmpty()) {
            recommendations.addAll(Arrays.asList(
                "ATT INFO: Review SLA compliance rates | Reason: Ensure timely FTTH installations",
                "ATT INFO: Validate fiber optic documentation | Reason: Prevent installation delays",
                "ATT INFO: Monitor technician resource allocation | Reason: Optimize deployment efficiency"
            ));
        }
        
        return recommendations;
    }

    private String formatRecommendation(String line) {
        try {
            // Skip if line appears to be a risk area header
            if (line.toLowerCase().contains("risk area") || 
                line.toLowerCase().contains("low") || 
                line.toLowerCase().contains("medium") || 
                line.toLowerCase().contains("high")) {
                return null;
            }
            
            // Check if line contains a separator
            if (line.contains("|")) {
                String[] parts = line.split("\\|", 2);
                String action = parts[0].trim();
                String reason = parts[1].trim();
                
                // Add prefixes if missing
                if (!action.toLowerCase().startsWith("att info:")) {
                    action = "ATT INFO: " + action;
                }
                if (!reason.toLowerCase().startsWith("reason:")) {
                    reason = "Reason: " + reason;
                }
                
                return action + " | " + reason;
            } else if (line.contains(":")) {
                // Try to split on colon
                String[] parts = line.split(":", 2);
                return String.format("ATT INFO: %s | Reason: %s", 
                    parts[0].trim(), 
                    parts.length > 1 ? parts[1].trim() : "Improve FTTH deployment efficiency"
                );
            } else if (!line.isEmpty()) {
                // Create recommendation from single line
                return String.format("ATT INFO: %s | Reason: Optimize FTTH operations", line);
            }
        } catch (Exception e) {
            log.debug("Error formatting recommendation: {}", line, e);
        }
        return null;
    }

    public String extractPrimarySolution(String response) {
        log.debug("Extracting primary solution from response");
        
        try {
            // Extract the Primary Action section
            String primarySection = extractSection(response, "Primary Action");
            if (primarySection.isEmpty()) {
                primarySection = extractSection(response, "Primary Solution");
            }
            
            if (!primarySection.isEmpty()) {
                // Split into lines and process
                String[] lines = primarySection.split("\n");
                for (String line : lines) {
                    line = line.trim();
                    
                    // Skip empty lines and section headers
                    if (line.isEmpty() || 
                        line.equalsIgnoreCase("Primary Action:") || 
                        line.equalsIgnoreCase("Primary Solution:")) {
                        continue;
                    }
                    
                    // Remove bullet points if present
                    if (line.startsWith("-") || line.startsWith("*") || line.startsWith("•")) {
                        line = line.substring(1).trim();
                    }
                    
                    // Return first non-empty meaningful line
                    if (line.length() > 10) {
                        log.debug("Found primary solution: {}", line);
                        return line;
                    }
                }
            }
            
            log.warn("No primary solution found in response");
            return null;
            
        } catch (Exception e) {
            log.error("Error extracting primary solution: {}", e.getMessage(), e);
            return null;
        }
    }
}