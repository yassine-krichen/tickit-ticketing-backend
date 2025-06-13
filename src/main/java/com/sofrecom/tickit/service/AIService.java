package com.sofrecom.tickit.service;

public interface AIService {
    String query(String prompt);
    String getEnhancedContext(String ticketSubject, String ticketType, String status);
    public String queryWithContext(String prompt, String additionalContext);
}
