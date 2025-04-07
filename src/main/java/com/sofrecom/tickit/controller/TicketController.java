package com.sofrecom.tickit.controller;

import com.sofrecom.tickit.dto.TicketRequest;
import com.sofrecom.tickit.dto.TicketResponse;
import com.sofrecom.tickit.dto.TicketUpdateDTO;
import com.sofrecom.tickit.exception.TicketCreationException;
import com.sofrecom.tickit.model.Ticket;
import com.sofrecom.tickit.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(@Valid @RequestBody TicketRequest request) {
        try {
            Ticket ticket = convertRequestToEntity(request);
            Ticket createdTicket = ticketService.createTicket(ticket);

            TicketResponse response = convertEntityToResponse(createdTicket);
            response.setStatusMessage("SUCCESS");
            response.setMessage("Ticket created successfully");
            return ResponseEntity.ok(response);
        } catch (TicketCreationException e) {
            TicketResponse errorResponse = TicketResponse.builder()
                    .statusMessage("FAILURE")
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<TicketResponse> getTicketById(@PathVariable UUID ticketId) {
        Ticket ticket = ticketService.getTicketById(ticketId);
        if (ticket == null) {
            return ResponseEntity.notFound().build();
        }
        TicketResponse response = convertEntityToResponse(ticket);
        return ResponseEntity.ok(response);
    }

    @GetMapping
public ResponseEntity<List<TicketResponse>> getAllTickets(
        @RequestParam(name = "status", required = false) Ticket.Status status,
        @RequestParam(name = "priority", required = false) Ticket.Priority priority,
        @RequestParam(name = "issuedBy", required = false) String issuedBy,
        @RequestParam(name = "acteTraitement", required = false) String acteTraitement) {
    
    List<Ticket> tickets = ticketService.getAllTickets(status, priority, issuedBy, acteTraitement);
    List<TicketResponse> responses = tickets.stream()
            .map(this::convertEntityToResponse)
            .collect(Collectors.toList());
    return ResponseEntity.ok(responses);
}


    @PutMapping("/{ticketId}/status")
    public ResponseEntity<TicketResponse> updateTicketStatus( 
            @PathVariable UUID ticketId, 
            @RequestParam Ticket.Status status) {
        try {
            Ticket updatedTicket = ticketService.updateTicketStatus(ticketId, status);
            TicketResponse response = convertEntityToResponse(updatedTicket);
            response.setStatusMessage("SUCCESS");
            response.setMessage("Ticket status updated successfully");
            return ResponseEntity.ok(response);
        } catch (TicketCreationException e) {
            TicketResponse errorResponse = TicketResponse.builder()
                    .statusMessage("FAILURE")
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PutMapping("/{ticketId}")
    public ResponseEntity<TicketResponse> updateTicket(
            @PathVariable UUID ticketId, 
            @Valid @RequestBody TicketUpdateDTO request) {
        try {
            Ticket updatedTicket = ticketService.updateTicket(ticketId, request);
            TicketResponse response = convertEntityToResponse(updatedTicket);
            response.setStatusMessage("SUCCESS");
            response.setMessage("Ticket updated successfully");
            return ResponseEntity.ok(response);
        } catch (TicketCreationException e) {
            TicketResponse errorResponse = TicketResponse.builder()
                    .statusMessage("FAILURE")
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @DeleteMapping("/{ticketId}")
    public ResponseEntity<String> deleteTicket(@PathVariable UUID ticketId) {
        try {
            ticketService.deleteTicket(ticketId);
            return ResponseEntity.ok("Ticket deleted successfully");
        } catch (TicketCreationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error deleting ticket: " + e.getMessage());
        }
    }

    // Helper methods for conversion

    private Ticket convertRequestToEntity(TicketRequest request) {
        return Ticket.builder()
                .acteTraitement(request.getActeTraitement())
                .subject(request.getSubject())
                .description(request.getDescription())
                .priority(request.getPriority())
                .status(request.getStatus())
                .issuedBy(request.getIssuedBy())
                .build();
    }

    private TicketResponse convertEntityToResponse(Ticket ticket) {
        return TicketResponse.builder()
                .id(ticket.getId())
                .acteTraitement(ticket.getActeTraitement())
                .subject(ticket.getSubject())
                .description(ticket.getDescription())
                .priority(ticket.getPriority())
                .status(ticket.getStatus())
                .issuedBy(ticket.getIssuedBy())
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .build();
    }
}
