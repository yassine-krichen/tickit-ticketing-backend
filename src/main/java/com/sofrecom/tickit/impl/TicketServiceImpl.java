package com.sofrecom.tickit.impl;

import com.sofrecom.tickit.dto.TicketCount;
import com.sofrecom.tickit.dto.TicketUpdateDTO;
import com.sofrecom.tickit.exception.EntityCreationException;
import com.sofrecom.tickit.model.Ticket;
import com.sofrecom.tickit.repository.TicketRepository;
import com.sofrecom.tickit.service.TicketService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;

    @Override
    public Ticket createTicket(Ticket ticket) throws EntityCreationException {
        try {
            LocalDateTime now = LocalDateTime.now();
            ticket.setCreatedAt(now);
            return ticketRepository.save(ticket);
        } catch (Exception e) {
            throw new EntityCreationException("Ticket", "Failed to create ticket", e);
        }
    }

    @Override
    public Ticket getTicketById(UUID ticketId) {
        return ticketRepository.findById(ticketId).orElse(null);
    }  

    @Override
public List<Ticket> getAllTickets(Ticket.Status status, Ticket.Priority priority, String issuedBy, String acteTraitement) {
    Specification<Ticket> spec = Specification.where(null);
    
    if (status != null) {
        spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
    }
    if (priority != null) {
        spec = spec.and((root, query, cb) -> cb.equal(root.get("priority"), priority));
    }
    if (issuedBy != null && !issuedBy.isEmpty()) {
        spec = spec.and((root, query, cb) -> cb.equal(root.get("issuedBy"), issuedBy));
    }
    if (acteTraitement != null && !acteTraitement.isEmpty()) {
        spec = spec.and((root, query, cb) -> cb.equal(root.get("acteTraitement"), acteTraitement));
    }
    
    // If no filter is provided, spec will be null and findAll(spec) returns all records.
    return ticketRepository.findAll(spec);
}

    @Override
    public Ticket updateTicketStatus(UUID ticketId, Ticket.Status status) throws EntityCreationException {
        Ticket ticket = getTicketById(ticketId);
        if (ticket == null) {
            throw new EntityCreationException("Ticket", "Ticket not found");
        }
        ticket.setStatus(status);
        ticket.setUpdatedAt(LocalDateTime.now());
        return saveTicket(ticket);
    }

    @Override
    public Ticket updateTicket(UUID ticketId, TicketUpdateDTO ticketRequest) throws EntityCreationException {
        Ticket ticket = getTicketById(ticketId);
        if (ticket == null) {
            throw new EntityCreationException("Ticket", "Ticket not found");
        }
    
    // Update only if provided fields are not null or empty (ignore acteTraitement)
    if (ticketRequest.getSubject() != null && !ticketRequest.getSubject().trim().isEmpty()) {
        ticket.setSubject(ticketRequest.getSubject());
    }
    if (ticketRequest.getDescription() != null && !ticketRequest.getDescription().trim().isEmpty()) {
        ticket.setDescription(ticketRequest.getDescription());
    }
    if (ticketRequest.getPriority() != null) {
        ticket.setPriority(ticketRequest.getPriority());
    }
    if (ticketRequest.getStatus() != null) {
        ticket.setStatus(ticketRequest.getStatus());
    }
    if (ticketRequest.getIssuedBy() != null && !ticketRequest.getIssuedBy().trim().isEmpty()) {
        ticket.setIssuedBy(ticketRequest.getIssuedBy());
    }

    // Note: We intentionally do not update acteTraitement.
    
    // Update the modification timestamp
    ticket.setUpdatedAt(LocalDateTime.now());
    
    return saveTicket(ticket);
}

    @Override
    public void deleteTicket(UUID ticketId) throws EntityCreationException {
        Ticket ticket = getTicketById(ticketId);
        if (ticket == null) {
            throw new EntityCreationException("Ticket", "Ticket not found");
        }
        ticketRepository.delete(ticket);
    }

    private Ticket saveTicket(Ticket ticket) throws EntityCreationException {
        try {
            return ticketRepository.save(ticket);
        } catch (Exception e) {
            throw new EntityCreationException("Ticket", "Failed to save ticket", e);
        }
    }

    @Override
    public List<TicketCount> getTicketCountByStatus() {
        return ticketRepository.countTicketsByStatus();
    }
    
}
