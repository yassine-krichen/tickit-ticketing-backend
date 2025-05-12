package com.sofrecom.tickit.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import java.nio.charset.StandardCharsets;

/**
 * Handler for WebSocket exceptions and STOMP errors.
 * Provides custom error responses for WebSocket communication issues.
 */
@Component
@Slf4j
public class WebSocketExceptionHandler extends StompSubProtocolErrorHandler {

    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
        log.error("WebSocket client message processing error: {}", ex.getMessage(), ex);
        
        // Extract the STOMP headers from the client message
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(clientMessage);
        
        // Prepare a more friendly error message
        String errorMessage = "Error processing message: " + ex.getMessage();
        
        // Build the error frame with proper headers
        StompHeaderAccessor errorHeaderAccessor = StompHeaderAccessor.create(StompCommand.ERROR);
        errorHeaderAccessor.setMessage(errorMessage);
        errorHeaderAccessor.setSessionId(headerAccessor.getSessionId());
        if (headerAccessor.getReceipt() != null) {
            errorHeaderAccessor.setReceiptId(headerAccessor.getReceipt());
        }
        
        return MessageBuilder.createMessage(
                errorMessage.getBytes(StandardCharsets.UTF_8),
                errorHeaderAccessor.getMessageHeaders()
        );
    }

    /**
     * Handles internal errors in the WebSocket protocol.
     * This method corrects the signature to match the parent class.
     */
    @Override
    protected Message<byte[]> handleInternal(StompHeaderAccessor errorHeaderAccessor, 
                                           byte[] errorPayload, 
                                           Throwable cause, 
                                           StompHeaderAccessor clientHeaderAccessor) {
        log.error("WebSocket internal error: {}", cause.getMessage(), cause);
        
        if (cause instanceof MessageDeliveryException) {
            return super.handleInternal(errorHeaderAccessor, errorPayload, cause, clientHeaderAccessor);
        }
        
        return super.handleInternal(errorHeaderAccessor, errorPayload, cause, clientHeaderAccessor);
    }
}