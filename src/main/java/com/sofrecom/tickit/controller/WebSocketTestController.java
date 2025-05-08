package com.sofrecom.tickit.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for testing WebSocket connectivity.
 */
@Controller
@Slf4j
public class WebSocketTestController {

    /**
     * Echo endpoint that returns a message to the sender with authentication status.
     */
    @MessageMapping("/echo")
    @SendToUser("/queue/echo")
    public Map<String, Object> echo(String message, Principal principal) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("timestamp", System.currentTimeMillis());
        
        if (principal != null) {
            response.put("authenticated", true);
            response.put("username", principal.getName());
            log.info("Echo request from user: {}", principal.getName());
        } else {
            response.put("authenticated", false);
            log.warn("Echo request from unauthenticated user");
        }
        
        return response;
    }
}