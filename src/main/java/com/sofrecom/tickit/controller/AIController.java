package com.sofrecom.tickit.controller;

import com.sofrecom.tickit.dto.AIAnalysisResponse;
import com.sofrecom.tickit.dto.AIResolutionResponse;
import com.sofrecom.tickit.dto.ErrorResponse;
import com.sofrecom.tickit.exception.ResourceNotFoundException;
import com.sofrecom.tickit.service.AIService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "AI Analysis", description = "AI-powered analysis and predictions for FTTH deployment system")
public class AIController {

    private final AIService aiService;

    @PostMapping("/analyse")
    @Operation(
        summary = "Analyze system performance",
        description = "Analyzes FTTH deployment system performance metrics, trends, and risks"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Analysis completed successfully",
            content = @Content(schema = @Schema(implementation = AIAnalysisResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Not authorized to access AI analysis",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error during analysis",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<AIAnalysisResponse> analyzeSystem() {
        log.info("Starting system performance analysis");
        try {
            AIAnalysisResponse analysis = aiService.analyzeSystemStatistics();
            log.info("Successfully completed system analysis with {} metrics and {} recommendations",
                    analysis.getPerformanceMetrics().size(),
                    analysis.getRecommendations().size());
            return ResponseEntity.ok(analysis);
        } catch (Exception e) {
            log.error("Error during system analysis: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to complete system analysis", e);
        }
    }

    @PostMapping("/suggest/{ticketId}")
    @Operation(
        summary = "Get resolution suggestions",
        description = "Generate AI-powered resolution suggestions for a specific ticket based on historical data"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Resolution suggestions generated successfully",
            content = @Content(schema = @Schema(implementation = AIResolutionResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Ticket not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Not authorized to access ticket resolutions",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error while generating suggestions",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<AIResolutionResponse> suggestResolution(
            @Parameter(description = "UUID of the ticket requiring resolution", required = true)
            @PathVariable @Valid UUID ticketId) {
        
        log.info("Generating resolution suggestions for ticket {}", ticketId);
        
        try {
            AIResolutionResponse resolution = aiService.suggestResolution(ticketId);
            
            log.info("Successfully generated resolution for ticket {} with {} steps and {} alternatives",
                    ticketId,
                    resolution.getRequiredSteps().size(),
                    resolution.getAlternativeSolutions().size());
            
            return ResponseEntity.ok(resolution);
            
        } catch (ResourceNotFoundException e) {
            log.warn("Ticket {} not found", ticketId);
            throw e;
        } catch (Exception e) {
            log.error("Error generating resolution for ticket {}: {}", ticketId, e.getMessage(), e);
            throw new RuntimeException("Failed to generate resolution suggestions", e);
        }
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(LocalDateTime.now(),
            HttpStatus.NOT_FOUND.value(),
            "Resource Not Found",
            ex.getMessage(),null, null
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "An unexpected error occurred while processing your request", null, null
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
