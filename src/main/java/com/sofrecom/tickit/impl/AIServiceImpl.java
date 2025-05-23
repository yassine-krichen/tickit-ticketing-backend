package com.sofrecom.tickit.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sofrecom.tickit.service.AIService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AIServiceImpl implements AIService {

    @Value("${llm.endpoint}")
    private String llmEndpoint;

    @Value("${llm.model}")
    private String modelName;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String query(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);

        Map<String, Object> body = new HashMap<>();
        body.put("model", modelName);
        body.put("messages", Collections.singletonList(message));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        @SuppressWarnings("unchecked")
        Map<String, Object> response = (Map<String, Object>) restTemplate.postForObject(llmEndpoint, request, Map.class);
        if (response == null || !response.containsKey("choices")) {
            return null;
        }

        Object choicesObj = response.get("choices");
        List<Map<String, Object>> choices = null;
        if (choicesObj != null) {
            ObjectMapper mapper = new ObjectMapper();
            choices = mapper.convertValue(choicesObj, mapper.getTypeFactory().constructCollectionType(List.class, Map.class));
        }
        if (choices == null || choices.isEmpty()) {
            return null;
        }

        Map<String, Object> firstChoice = choices.get(0);
        Map<String, Object> messageOut = null;
        Object messageObj = firstChoice.get("message");
        if (messageObj != null) {
            ObjectMapper mapper = new ObjectMapper();
            messageOut = mapper.convertValue(messageObj, mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class));
        }

        return messageOut != null ? (String) messageOut.get("content") : null;
    }

}