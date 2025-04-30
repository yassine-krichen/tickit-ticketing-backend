package com.sofrecom.tickit.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sofrecom.tickit.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Custom handler for access denied exceptions.
 * Provides a structured JSON response when a user attempts to access a resource without proper authorization.
 */
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, 
                        AccessDeniedException accessDeniedException) throws IOException, ServletException {
        
        // Create a structured error response
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                "You don't have permission to access this resource",
                request.getRequestURI()
        );

        // Configure the response
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // Write the error response as JSON
        new ObjectMapper().writeValue(response.getOutputStream(), errorResponse);
    }
}