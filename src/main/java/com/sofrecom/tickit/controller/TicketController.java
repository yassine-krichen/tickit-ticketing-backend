package com.sofrecom.tickit.controller;

import com.sofrecom.tickit.dto.PaginatedResponse;
import com.sofrecom.tickit.dto.TicketCount;
import com.sofrecom.tickit.dto.TicketRequest;
import com.sofrecom.tickit.dto.TicketResponse;
import com.sofrecom.tickit.dto.TicketUpdateDTO;
import com.sofrecom.tickit.exception.EntityCreationException;
import com.sofrecom.tickit.mapper.TicketMapper;
import com.sofrecom.tickit.model.CaseType;
import com.sofrecom.tickit.model.Ticket;
import com.sofrecom.tickit.service.TicketService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.HashMap;

/**
 * REST controller for managing Tickets.
 */
@RestController
@RequestMapping("/api/v1/tickets") // Updated base path
@RequiredArgsConstructor
@Validated // Enables validation of method parameters annotated with constraints
@Tag(name = "Ticket Management", description = "APIs for ticket operations in the ticketing system")
public class TicketController {

    private final TicketService ticketService;
    private final TicketMapper ticketMapper;

    /**
     * Retrieves a paginated list of tickets based on specified criteria.
     *
     * @param page      Page number (0-indexed).
     * @param size      Number of items per page (1-100).
     * @param sortField Field to sort by (timestamp, title, priority, status).
     * @param sortDir   Sort direction (asc, desc).
     * @param q         Optional search query for subject/description.
     * @param status    Optional filter by ticket status.
     * @param priority  Optional filter by ticket priority.
     * @return A ResponseEntity containing the paginated list of tickets or an error response.
     */
    @Operation(summary = "Get paginated tickets",
               description = "Returns a paginated list of tickets with filtering and sorting options.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                    description = "Successfully retrieved tickets",
                    content = @Content(mediaType = "application/json",
                                       schema = @Schema(implementation = PaginatedResponseTicketResponseSchema.class))), // Use wrapper schema
        @ApiResponse(responseCode = "400",
                    description = "Invalid input parameters (page, size, sortField, sortDir)",
                    content = @Content(mediaType = "application/json",
                                       schema = @Schema(implementation = com.sofrecom.tickit.dto.ErrorResponse.class))) // Reference ErrorResponse DTO
    })
    @GetMapping
    public ResponseEntity<PaginatedResponse<TicketResponse>> getTickets(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) int page,

            @Parameter(description = "Number of items per page", example = "20")
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,

            @Parameter(description = "Field to sort by. Allowed values: timestamp, title, priority, status", example = "timestamp")
            @RequestParam(defaultValue = "timestamp")
            // Basic pattern check, more specific validation in service
            @Pattern(regexp = "^(timestamp|title|priority|status)$", message = "Invalid sort field. Allowed: timestamp, title, priority, status")
            String sortField,

            @Parameter(description = "Sort direction. Allowed values: asc, desc", example = "desc")
            @RequestParam(defaultValue = "desc")
            @Pattern(regexp = "^(asc|desc)$", message = "Invalid sort direction. Allowed: asc, desc")
            String sortDir,

            @Parameter(description = "Optional free-text search across subject and description", example = "database connection")
            @RequestParam(required = false) String q,

            @Parameter(description = "Optional filter by ticket status", example = "OPEN")
            @RequestParam(required = false) Ticket.Status status,

            @Parameter(description = "Optional filter by ticket priority", example = "HIGH")
            @RequestParam(required = false) Ticket.Priority priority
    ) {
        // Service layer handles validation of sortField/sortDir logic and performs the query
        Page<Ticket> ticketPage = ticketService.getTickets(page, size, sortField, sortDir, q, status, priority);

        // Map Page<Ticket> to PaginatedResponse<TicketResponse>
        PaginatedResponse<TicketResponse> response = ticketMapper.toPaginatedResponse(ticketPage);

        return ResponseEntity.ok(response);
    }

    // --- Helper Schema Class for OpenAPI ---
    // This is needed because OpenAPI annotations cannot directly reference generic types like PaginatedResponse<TicketResponse>.
    @Schema(name = "PaginatedTicketResponse")
    private static class PaginatedResponseTicketResponseSchema extends PaginatedResponse<TicketResponse> {}

    @Operation(summary = "Create a new ticket", 
               description = "Creates a new ticket in the system based on the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Ticket created successfully",
                    content = @Content(schema = @Schema(implementation = TicketResponse.class))),
        @ApiResponse(responseCode = "400", 
                    description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = TicketResponse.class)))
    })
    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(
            @Parameter(description = "Ticket details to create", required = true) 
            @Valid @RequestBody TicketRequest request) {
        try {
            Ticket ticket = ticketMapper.toEntity(request);
            Ticket createdTicket = ticketService.createTicket(ticket);

            TicketResponse response = ticketMapper.toResponse(createdTicket);
            response.setStatusMessage("SUCCESS");
            response.setMessage("Ticket created successfully");
            return ResponseEntity.ok(response);
        } catch (EntityCreationException e) {
            TicketResponse errorResponse = TicketResponse.builder()
                    .statusMessage("FAILURE")
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @Operation(summary = "Get ticket by ID", 
               description = "Returns a ticket based on its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Ticket found",
                    content = @Content(schema = @Schema(implementation = TicketResponse.class))),
        @ApiResponse(responseCode = "404", 
                    description = "Ticket not found")
    })
    @GetMapping("/{ticketId}")
    public ResponseEntity<TicketResponse> getTicketById(
            @Parameter(description = "Unique identifier of the ticket", required = true) 
            @PathVariable UUID ticketId) {
        Ticket ticket = ticketService.getTicketById(ticketId);
        if (ticket == null) {
            return ResponseEntity.notFound().build();
        }
        TicketResponse response = ticketMapper.toResponse(ticket);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all tickets", 
               description = "Returns all tickets with optional filtering by status, priority, issuer, or acte traitement")
    @ApiResponse(responseCode = "200", 
                description = "List of tickets matching the filter criteria",
                content = @Content(schema = @Schema(implementation = TicketResponse.class)))
    @GetMapping("/all")
    public ResponseEntity<List<TicketResponse>> getAllTickets(
            @Parameter(description = "Filter by ticket status") 
            @RequestParam(name = "status", required = false) Ticket.Status status,
            @Parameter(description = "Filter by ticket priority") 
            @RequestParam(name = "priority", required = false) Ticket.Priority priority,
            @Parameter(description = "Filter by user who issued the ticket") 
            @RequestParam(name = "issuedBy", required = false) String issuedBy,
            @Parameter(description = "Filter by associated acte traitement ID") 
            @RequestParam(name = "acteTraitement", required = false) String acteTraitement) {
        
        List<Ticket> tickets = ticketService.getAllTickets(status, priority, issuedBy, acteTraitement);
        List<TicketResponse> responses = tickets.stream()
                .map(ticketMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Search tickets with enhanced filters", 
               description = "Search tickets by various criteria including type, step, and sub-status")
    @ApiResponse(responseCode = "200", 
                description = "List of tickets matching the search criteria",
                content = @Content(schema = @Schema(implementation = TicketResponse.class)))
    @GetMapping("/search")
    public ResponseEntity<List<TicketResponse>> searchTickets(
            @Parameter(description = "Filter by ticket status") 
            @RequestParam(name = "status", required = false) Ticket.Status status,
            @Parameter(description = "Filter by ticket priority") 
            @RequestParam(name = "priority", required = false) Ticket.Priority priority,
            @Parameter(description = "Filter by user who issued the ticket") 
            @RequestParam(name = "issuedBy", required = false) String issuedBy,
            @Parameter(description = "Filter by associated acte traitement ID") 
            @RequestParam(name = "acteTraitement", required = false) String acteTraitement,
            @Parameter(description = "Filter by case type") 
            @RequestParam(name = "type", required = false) CaseType type,
            @Parameter(description = "Filter by case step") 
            @RequestParam(name = "step", required = false) CaseType step,
            @Parameter(description = "Filter by sub-status") 
            @RequestParam(name = "subStatus", required = false) String subStatus) {
        
        List<Ticket> tickets = ticketService.searchTickets(status, priority, issuedBy, acteTraitement, type, step, subStatus);
        List<TicketResponse> responses = tickets.stream()
                .map(ticketMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get ticket counts by status", 
               description = "Returns ticket counts grouped by their status")
    @ApiResponse(responseCode = "200", 
                description = "Ticket counts by status",
                content = @Content(schema = @Schema(implementation = TicketCount.class)))
    @GetMapping("/counts-by-status")
    public ResponseEntity<List<TicketCount>> getTicketCountByStatus() {
        List<TicketCount> counts = ticketService.getTicketCountByStatus();
        return ResponseEntity.ok(counts);
    }
    
    @Operation(summary = "Get ticket counts by type", 
               description = "Returns ticket counts grouped by their type")
    @ApiResponse(responseCode = "200", 
                description = "Ticket counts by type",
                content = @Content(schema = @Schema(implementation = TicketCount.class)))
    @GetMapping("/counts-by-type")
    public ResponseEntity<List<TicketCount>> getTicketCountByType() {
        List<TicketCount> counts = ticketService.getTicketCountByType();
        return ResponseEntity.ok(counts);
    }
    
    @Operation(summary = "Get ticket counts by step", 
               description = "Returns ticket counts grouped by their step")
    @ApiResponse(responseCode = "200", 
                description = "Ticket counts by step",
                content = @Content(schema = @Schema(implementation = TicketCount.class)))
    @GetMapping("/counts-by-step")
    public ResponseEntity<List<TicketCount>> getTicketCountByStep() {
        List<TicketCount> counts = ticketService.getTicketCountByStep();
        return ResponseEntity.ok(counts);
    }
    
    @Operation(summary = "Get ticket counts by sub-status", 
               description = "Returns ticket counts grouped by their sub-status")
    @ApiResponse(responseCode = "200", 
                description = "Ticket counts by sub-status",
                content = @Content(schema = @Schema(implementation = TicketCount.class)))
    @GetMapping("/counts-by-sub-status")
    public ResponseEntity<List<TicketCount>> getTicketCountBySubStatus() {
        List<TicketCount> counts = ticketService.getTicketCountBySubStatus();
        return ResponseEntity.ok(counts);
    }
    
    @Operation(summary = "Get ticket statistics by period", 
               description = "Returns ticket statistics for a specific period (daily, weekly, monthly, quarterly, yearly)")
    @ApiResponse(responseCode = "200", 
                description = "Ticket statistics for the specified period")
    @GetMapping("/stats/{period}")
    public ResponseEntity<Map<String, Long>> getTicketStatsByPeriod(
            @Parameter(description = "Period for statistics (daily, weekly, monthly, quarterly, yearly)", required = true) 
            @PathVariable String period) {
        Map<String, Long> stats = ticketService.getTicketStatsByPeriod(period);
        return ResponseEntity.ok(stats);
    }
    
    @Operation(summary = "Validate type-step combination", 
               description = "Checks if a given case type and step combination is valid")
    @ApiResponse(responseCode = "200", 
                description = "Validation result")
    @GetMapping("/validate-type-step")
    public ResponseEntity<Boolean> validateTypeStepCombination(
            @Parameter(description = "Case type", required = true) 
            @RequestParam CaseType type,
            @Parameter(description = "Case step", required = true) 
            @RequestParam CaseType step) {
        boolean isValid = ticketService.isValidTypeStepCombination(type, step);
        return ResponseEntity.ok(isValid);
    }
    
    @Operation(summary = "Get valid steps for type", 
               description = "Returns all valid steps for a given case type")
    @ApiResponse(responseCode = "200", 
                description = "List of valid steps for the specified type")
    @GetMapping("/valid-steps/{type}")
    public ResponseEntity<List<CaseType>> getValidStepsForType(
            @Parameter(description = "Case type", required = true) 
            @PathVariable CaseType type) {
        List<CaseType> validSteps = ticketService.getValidStepsForType(type);
        return ResponseEntity.ok(validSteps);
    }
   
    @Operation(summary = "Update ticket status", 
               description = "Updates the status of an existing ticket")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Ticket status updated successfully",
                    content = @Content(schema = @Schema(implementation = TicketResponse.class))),
        @ApiResponse(responseCode = "400", 
                    description = "Invalid status update request",
                    content = @Content(schema = @Schema(implementation = TicketResponse.class))),
        @ApiResponse(responseCode = "404", 
                    description = "Ticket not found")
    })
    @PutMapping("/{ticketId}/status")
    public ResponseEntity<TicketResponse> updateTicketStatus( 
            @Parameter(description = "Unique identifier of the ticket", required = true) 
            @PathVariable UUID ticketId, 
            @Parameter(description = "New status value", required = true) 
            @RequestParam Ticket.Status status) {
        try {
            Ticket updatedTicket = ticketService.updateTicketStatus(ticketId, status);
            TicketResponse response = ticketMapper.toResponse(updatedTicket);
            response.setStatusMessage("SUCCESS");
            response.setMessage("Ticket status updated successfully");
            return ResponseEntity.ok(response);
        } catch (EntityCreationException e) {
            TicketResponse errorResponse = TicketResponse.builder()
                    .statusMessage("FAILURE")
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @Operation(summary = "Update ticket", 
               description = "Updates the details of an existing ticket")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Ticket updated successfully",
                    content = @Content(schema = @Schema(implementation = TicketResponse.class))),
        @ApiResponse(responseCode = "400", 
                    description = "Invalid update request",
                    content = @Content(schema = @Schema(implementation = TicketResponse.class))),
        @ApiResponse(responseCode = "404", 
                    description = "Ticket not found")
    })
    @PutMapping("/{ticketId}")
    public ResponseEntity<TicketResponse> updateTicket(
            @Parameter(description = "Unique identifier of the ticket", required = true) 
            @PathVariable UUID ticketId, 
            @Parameter(description = "Updated ticket details", required = true) 
            @Valid @RequestBody TicketUpdateDTO request) {
        try {
            Ticket updatedTicket = ticketService.updateTicket(ticketId, request);
            TicketResponse response = ticketMapper.toResponse(updatedTicket);
            response.setStatusMessage("SUCCESS");
            response.setMessage("Ticket updated successfully");
            return ResponseEntity.ok(response);
        } catch (EntityCreationException e) {
            TicketResponse errorResponse = TicketResponse.builder()
                    .statusMessage("FAILURE")
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @Operation(summary = "Delete a ticket", 
               description = "Deletes a ticket by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Ticket deleted successfully",
                    content = @Content(schema = @Schema(type = "string"))),
        @ApiResponse(responseCode = "400", 
                    description = "Error deleting ticket",
                    content = @Content(schema = @Schema(type = "string"))),
        @ApiResponse(responseCode = "404", 
                    description = "Ticket not found")
    })
    @DeleteMapping("/{ticketId}")
    public ResponseEntity<String> deleteTicket(
            @Parameter(description = "Unique identifier of the ticket to delete", required = true) 
            @PathVariable UUID ticketId) {
        try {
            ticketService.deleteTicket(ticketId);
            return ResponseEntity.ok("Ticket deleted successfully");
        } catch (EntityCreationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error deleting ticket: " + e.getMessage());
        }
    }

    @Operation(summary = "Get all ticket statistics", 
               description = "Returns all ticket statistics including counts by status, type, step, and sub-status")
    @ApiResponse(responseCode = "200", 
                description = "All ticket statistics")
    @GetMapping("/stats/all")
    public ResponseEntity<Map<String, Object>> getAllTicketStats() {
        Map<String, Object> allStats = new HashMap<>();
        
        // Get all statistics
        allStats.put("countsByStatus", ticketService.getTicketCountByStatus());
        allStats.put("countsByType", ticketService.getTicketCountByType());
        allStats.put("countsByStep", ticketService.getTicketCountByStep());
        allStats.put("countsBySubStatus", ticketService.getTicketCountBySubStatus());
        
        // Add raw counts for debugging
        allStats.put("totalTickets", ticketService.getAllTickets(null, null, null, null).size());
        
        return ResponseEntity.ok(allStats);
    }
}
