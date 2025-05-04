package com.sofrecom.tickit.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import javax.validation.constraints.Pattern;

import com.sofrecom.tickit.dto.PaginatedResponse;
import com.sofrecom.tickit.dto.TicketActivityRequest;
import com.sofrecom.tickit.dto.TicketActivityResponse;
import com.sofrecom.tickit.exception.InvalidSortParameterException;
import com.sofrecom.tickit.model.TicketActivity.ActivityType;
import com.sofrecom.tickit.service.TicketActivityService;
import com.sofrecom.tickit.util.AuthUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

/**
 * REST controller for managing ticket activities.
 * Provides endpoints for creating, updating, completing, and retrieving ticket activities.
 */
@RestController
@RequestMapping("/api/v1/tickets/{ticketId}/activities")
@Tag(name = "Ticket Activities", description = "Operations related to ticket activities")
@Validated
@RequiredArgsConstructor
public class TicketActivityController {

    private final TicketActivityService service;
    private final AuthUtils authUtils;

    /**
     * Creates a new activity for a ticket.
     *
     * @param ticketId the UUID of the ticket
     * @param request the activity request containing details
     * @return a response entity with the created activity
     */
    @Operation(summary = "Create a new ticket activity", 
               description = "Creates a new activity record associated with a specific ticket")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Activity created successfully", 
                    content = @Content(schema = @Schema(implementation = TicketActivityResponse.class))),
        @ApiResponse(responseCode = "400", 
                    description = "Invalid request data"),
        @ApiResponse(responseCode = "404", 
                    description = "Ticket not found")
    })
    @PostMapping
    public ResponseEntity<TicketActivityResponse> create(
            @Parameter(description = "Unique identifier of the ticket", required = true)
            @PathVariable UUID ticketId,
            @Parameter(description = "Activity details for creation", required = true)
            @RequestBody TicketActivityRequest request) {
        
        // Get authenticated username and override the request field
        String username = authUtils.getAuthenticatedUsername();
        request.setUsername(username);
        
        return ResponseEntity.ok(service.createActivity(ticketId, request));
    }

    /**
     * Updates an existing ticket activity.
     *
     * @param ticketId the UUID of the ticket
     * @param activityId the ID of the activity to update
     * @param request the updated activity data
     * @return a response entity with the updated activity
     */
    @Operation(summary = "Update a ticket activity", 
               description = "Updates an existing activity record with new information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Activity updated successfully", 
                    content = @Content(schema = @Schema(implementation = TicketActivityResponse.class))),
        @ApiResponse(responseCode = "400", 
                    description = "Invalid request data"),
        @ApiResponse(responseCode = "404", 
                    description = "Ticket or activity not found")
    })
    @PutMapping("/{activityId}")
    public ResponseEntity<TicketActivityResponse> update(
            @Parameter(description = "Unique identifier of the ticket", required = true)
            @PathVariable UUID ticketId,
            @Parameter(description = "Unique identifier of the activity to update", required = true)
            @PathVariable Long activityId,
            @Parameter(description = "Updated activity details", required = true)
            @RequestBody TicketActivityRequest request) {
        
        // Get authenticated username and override the request field
        String username = authUtils.getAuthenticatedUsername();
        request.setUsername(username);
        
        return ResponseEntity.ok(service.updateActivity(ticketId, activityId, request));
    }

    /**
     * Marks a ticket activity as completed.
     *
     * @param ticketId the UUID of the ticket
     * @param activityId the ID of the activity to mark as complete
     * @return a response entity with the completed activity
     */
    @Operation(summary = "Complete a ticket activity", 
               description = "Marks an activity as completed and records the end time")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Activity marked as complete successfully", 
                    content = @Content(schema = @Schema(implementation = TicketActivityResponse.class))),
        @ApiResponse(responseCode = "404", 
                    description = "Ticket or activity not found")
    })
    @PutMapping("/{activityId}/complete")
    public ResponseEntity<TicketActivityResponse> complete(
            @Parameter(description = "Unique identifier of the ticket", required = true)
            @PathVariable UUID ticketId,
            @Parameter(description = "Unique identifier of the activity to complete", required = true)
            @PathVariable Long activityId) {
        return ResponseEntity.ok(service.completeActivity(ticketId, activityId));
    }

    /**
     * Deletes a ticket activity (soft delete).
     *
     * @param ticketId the UUID of the ticket
     * @param activityId the ID of the activity to delete
     * @return a response entity with no content
     */
    @Operation(summary = "Delete a ticket activity", 
               description = "Soft-deletes an activity record from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", 
                    description = "Activity deleted successfully"),
        @ApiResponse(responseCode = "404", 
                    description = "Ticket or activity not found")
    })
    @DeleteMapping("/{activityId}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Unique identifier of the ticket", required = true)
            @PathVariable UUID ticketId,
            @Parameter(description = "Unique identifier of the activity to delete", required = true)
            @PathVariable Long activityId) {
        service.deleteActivity(ticketId, activityId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves a paginated timeline of activities for a ticket.
     *
     * @param ticketId the UUID of the ticket
     * @param page the page number (zero-based)
     * @param size the page size
     * @param q optional search query for description text
     * @param type optional filter by activity type
     * @param username optional filter by username
     * @param sortField field to sort by (startTime, createdAt, type, username)
     * @param sortDir sort direction (asc, desc)
     * @return a paginated response of ticket activities
     */
    @Operation(summary = "Get ticket activity timeline", 
               description = "Retrieves a paginated list of activities for a specific ticket with optional filtering and sorting")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Activities retrieved successfully", 
                    content = @Content(schema = @Schema(implementation = PaginatedResponseTicketActivitySchema.class))),
        @ApiResponse(responseCode = "400", 
                    description = "Invalid input parameters (page, size, sortField, sortDir)"),
        @ApiResponse(responseCode = "404", 
                    description = "Ticket not found")
    })
    @GetMapping
    public ResponseEntity<PaginatedResponse<TicketActivityResponse>> timeline(
            @Parameter(description = "Unique identifier of the ticket", required = true)
            @PathVariable UUID ticketId,
            
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Number of items per page", example = "20")
            @RequestParam(defaultValue = "20") int size,
            
            @Parameter(description = "Optional search query for description text", example = "updated ticket")
            @RequestParam(required = false) String q,
            
            @Parameter(description = "Optional filter by activity type", example = "UPDATE")
            @RequestParam(required = false) ActivityType type,
            
            @Parameter(description = "Optional filter by username", example = "john.doe")
            @RequestParam(required = false) String username,
            
            @Parameter(description = "Field to sort by. Allowed values: startTime, createdAt, type, username", example = "startTime")
            @RequestParam(defaultValue = "startTime")
            @Pattern(regexp = "^(startTime|createdAt|type|username)$", 
                    message = "Invalid sort field. Allowed: startTime, createdAt, type, username")
            String sortField,
            
            @Parameter(description = "Sort direction. Allowed values: asc, desc", example = "desc")
            @RequestParam(defaultValue = "desc")
            @Pattern(regexp = "^(asc|desc)$", message = "Invalid sort direction. Allowed: asc, desc")
            String sortDir) {
        
        Page<TicketActivityResponse> pageResult = service.getTimeline(
                ticketId, 
                q, 
                type, 
                username, 
                page, 
                size, 
                sortField, 
                sortDir);
        
        PaginatedResponse<TicketActivityResponse> response = new PaginatedResponse<>(
                pageResult.getContent(),
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages(),
                pageResult.isLast()
        );
        
        return ResponseEntity.ok(response);
    }
    
    // Helper class for OpenAPI documentation
    @Schema(name = "PaginatedTicketActivityResponse")
    private static class PaginatedResponseTicketActivitySchema extends PaginatedResponse<TicketActivityResponse> {
        // This class exists only for OpenAPI documentation purposes
    }
}