package com.sofrecom.tickit.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

/**
 * Configures security rules for WebSocket messaging.
 * Intentionally minimal to avoid over-restricting connections.
 */
@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        // Allow all message types - authentication happens in channel interceptor
        messages
            // Allow connection establishment
            .nullDestMatcher().permitAll()
            // User-specific destinations require authentication
            .simpDestMatchers("/user/**").authenticated()
            // All other destinations - be permissive, validate in service layer
            .anyMessage().permitAll();
    }

    @Override
    protected boolean sameOriginDisabled() {
        // Disable CSRF for WebSocket connections
        return true;
    }
}