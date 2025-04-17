package com.sofrecom.tickit.service;

import com.sofrecom.tickit.model.Ticket;
import com.sofrecom.tickit.model.CaseType;
import com.sofrecom.tickit.dto.TicketCount;
import com.sofrecom.tickit.dto.TicketUpdateDTO;
import com.sofrecom.tickit.exception.EntityCreationException;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface TicketService {

    Ticket createTicket(Ticket ticket) throws EntityCreationException;

    Ticket getTicketById(UUID ticketId);

    List<Ticket> getAllTickets(Ticket.Status status, Ticket.Priority priority, String issuedBy, String acteTraitement);
    
    // Enhanced search with the new fields
    List<Ticket> searchTickets(Ticket.Status status, Ticket.Priority priority, String issuedBy, 
                              String acteTraitement, CaseType type, CaseType step, String subStatus);

    Ticket updateTicketStatus(UUID ticketId, Ticket.Status status) throws EntityCreationException;

    Ticket updateTicket(UUID ticketId, TicketUpdateDTO ticketRequest) throws EntityCreationException;

    void deleteTicket(UUID ticketId) throws EntityCreationException;

    List<TicketCount> getTicketCountByStatus();
    
    // New methods for SLA calculation
    Duration calculateResponseSLA(CaseType type, CaseType step, Ticket.Priority priority);
    
    Duration calculateResolutionSLA(CaseType type, CaseType step, Ticket.Priority priority);
    
    // New reporting methods
    List<TicketCount> getTicketCountByType();
    
    List<TicketCount> getTicketCountByStep();
    
    List<TicketCount> getTicketCountBySubStatus();
    
    Map<String, Long> getTicketStatsByPeriod(String period);
    
    // New method to validate type and step relationship
    boolean isValidTypeStepCombination(CaseType type, CaseType step);
    
    // Get valid steps for a given type
    List<CaseType> getValidStepsForType(CaseType type);
}
