package com.sofrecom.tickit.impl;

import com.sofrecom.tickit.model.Ticket;
import com.sofrecom.tickit.service.AIService;
import com.sofrecom.tickit.service.TicketService;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AIServiceImpl implements AIService {

    private static final Logger log = LoggerFactory.getLogger(AIServiceImpl.class);
    
    @Value("${llm.endpoint}")
    private String llmEndpoint;
    
    @Value("${llm.model}")
    private String modelName;
    
    private final RestTemplate restTemplate;
    private final TicketService ticketService;
    
    // public EnhancedAIServiceImpl(TicketService ticketService, ActeTraitementService acteTraitementService) {
    //     this.ticketService = ticketService;
    //     this.acteTraitementService = acteTraitementService;
    // }
    
    // Enhanced context retrieval for similar tickets
    @Override
    public String getEnhancedContext(String ticketSubject, String ticketType, String status) {
        if (ticketSubject == null || ticketType == null) {
            log.warn("Received null ticketSubject or ticketType");
            return "";
        }

        StringBuilder context = new StringBuilder();
        
        try {
            // Get similar resolved tickets for learning
            List<Ticket> similarTickets = ticketService.findSimilarResolvedTickets(
                ticketSubject, ticketType, 5); // Limit to 5 most similar
                
            if (!similarTickets.isEmpty()) {
                context.append("Similar Resolved Cases:\n");
                for (Ticket similar : similarTickets) {
                    context.append("- Subject: ").append(similar.getSubject())
                          .append(" | Status: ").append(similar.getStatus())
                          .append(" | Priority: ").append(similar.getPriority());
                    
                    // Calculate resolution time if resolvedAt is available
                    if (similar.getResolvedAt() != null && similar.getCreatedAt() != null) {
                        Duration resolutionDuration = Duration.between(similar.getCreatedAt(), similar.getResolvedAt());
                        long hours = resolutionDuration.toHours();
                        long minutes = resolutionDuration.toMinutes() % 60;
                        context.append(" | Resolution Time: ").append(hours).append("h ").append(minutes).append("m");
                    }
                    
                    // Add description if available for context
                    if (similar.getDescription() != null && !similar.getDescription().trim().isEmpty()) {
                        String shortDescription = similar.getDescription().length() > 100 
                            ? similar.getDescription().substring(0, 100) + "..." 
                            : similar.getDescription();
                        context.append(" | Description: ").append(shortDescription);
                    }
                    
                    context.append("\n");
                }
                context.append("\n");
            }
            
            // Get relevant knowledge base entries
            List<String> relevantKnowledge = getRelevantKnowledgeBase(ticketSubject, ticketType);
            if (!relevantKnowledge.isEmpty()) {
                context.append("Relevant Knowledge Base:\n");
                relevantKnowledge.forEach(kb -> context.append("- ").append(kb).append("\n"));
                context.append("\n");
            }
        } catch (Exception e) {
            log.error("Error building enhanced context", e);
            return "";
        }
        
        return context.toString();
    }
    
    @Override
    public String query(String prompt) {
        return queryWithContext(prompt, "");
    }
    
    // New method with enhanced context
    @Override
    public String queryWithContext(String prompt, String additionalContext) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        // Enhanced prompt with better structure
        String enhancedPrompt = buildEnhancedPrompt(prompt, additionalContext);
        
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", enhancedPrompt);
        
        Map<String, Object> body = new HashMap<>();
        body.put("model", modelName);
        body.put("messages", Arrays.asList(message));
        body.put("temperature", 0.3); // Lower temperature for more consistent responses
        body.put("max_tokens", 1000);
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(llmEndpoint, request, Map.class);
            return parseResponse(response);
        } catch (Exception e) {
            log.error("Error querying AI service", e);
            return "Error processing AI request";
        }
    }
    
    private String buildEnhancedPrompt(String originalPrompt, String context) {
        StringBuilder enhanced = new StringBuilder();
        enhanced.append("You are an expert IT support analyst. Use the following context to provide accurate, actionable advice.\n\n");
        
        if (context != null && !context.isEmpty()) {
            enhanced.append("CONTEXT:\n").append(context).append("\n");
        }
        
        enhanced.append("QUERY:\n").append(originalPrompt).append("\n\n");
        enhanced.append("Please provide a structured response with clear action items and reasoning.");
        
        return enhanced.toString();
    }
    
    private String parseResponse(Map<String, Object> response) {
        if (response == null || !response.containsKey("choices")) {
            return "No response received";
        }
        
        try {
            
            // Java 8 compatible approach for parsing choices
            Object choicesObj = response.get("choices");
            if (choicesObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> choices = (List<Map<String, Object>>) choicesObj;
                
                if (!choices.isEmpty()) {
                    Map<String, Object> firstChoice = choices.get(0);
                    Object messageObj = firstChoice.get("message");
                    
                    if (messageObj instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> message = (Map<String, Object>) messageObj;
                        Object content = message.get("content");
                        if (content instanceof String) {
                            return (String) content;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error parsing AI response", e);
        }
        
        return "Error parsing response";
    }
    
    // Simple keyword-based knowledge retrieval (can be enhanced with vector search later)
    private List<String> getRelevantKnowledgeBase(String subject, String type) {
        // For now, return static knowledge based on ticket type
        // In production, this would query a knowledge base or use semantic search
        Map<String, List<String>> knowledgeMap = new HashMap<>();
        
        knowledgeMap.put("TECHNICAL", Arrays.asList(
            "Check system logs for error patterns",
            "Verify network connectivity and firewall rules",
            "Review recent deployments or configuration changes"
        ));
        
        knowledgeMap.put("ACCESS", Arrays.asList(
            "Verify user permissions in identity management system",
            "Check account status and expiration dates",
            "Review role assignments and group memberships"
        ));
        
        knowledgeMap.put("PERFORMANCE", Arrays.asList(
            "Monitor system resources (CPU, memory, disk)",
            "Check for database query performance issues",
            "Review application logs for bottlenecks"
        ));
        
        String upperType = type != null ? type.toUpperCase() : "";
        return knowledgeMap.getOrDefault(upperType, new ArrayList<>());
    }
}