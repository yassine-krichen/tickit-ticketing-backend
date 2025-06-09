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
        // Authentication is enforced by WebSocketAuthChannelInterceptor at CONNECT time.
        // Destination-level security is not enforced here — @SendToUser already
        // restricts user-queue messages to the authenticated principal.
        messages.anyMessage().permitAll();
    }

    @Override
    protected boolean sameOriginDisabled() {
        // Disable CSRF for WebSocket connections
        return true;
    }
}