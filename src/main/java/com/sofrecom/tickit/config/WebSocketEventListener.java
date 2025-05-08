package com.sofrecom.tickit.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

/**
 * Listens for WebSocket session events to provide logging and session management.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketConnected(SessionConnectedEvent event) {
        Authentication auth = (Authentication) event.getUser();
        if (auth != null) {
            String username = auth.getName();
            log.info("WebSocket connection established for user: {}", username);
            
            // Optional: send welcome message to indicate successful connection
            messagingTemplate.convertAndSendToUser(
                username, 
                "/queue/info", 
                "Connected to notification service"
            );
        } else {
            log.debug("Anonymous WebSocket connection established");
        }
    }
    
    @EventListener
    public void handleWebSocketSubscribe(SessionSubscribeEvent event) {
        Authentication auth = (Authentication) event.getUser();
        if (auth != null) {
            String destination = event.getMessage().getHeaders().get("simpDestination", String.class);
            log.debug("User {} subscribed to {}", auth.getName(), destination);
        }
    }

    @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {
        Authentication auth = (Authentication) event.getUser();
        if (auth != null) {
            log.info("WebSocket connection closed for user: {}", auth.getName());
        }
    }
}