package com.sofrecom.tickit.service;

import com.sofrecom.tickit.model.Ticket;
import com.sofrecom.tickit.dto.TicketCount;
import com.sofrecom.tickit.dto.TicketUpdateDTO;
import com.sofrecom.tickit.exception.EntityCreationException;

import java.util.List;
import java.util.UUID;

public interface TicketService {

    Ticket createTicket(Ticket ticket) throws EntityCreationException;

    Ticket getTicketById(UUID ticketId);

    List<Ticket> getAllTickets(Ticket.Status status, Ticket.Priority priority, String issuedBy, String acteTraitement);

    Ticket updateTicketStatus(UUID ticketId, Ticket.Status status) throws EntityCreationException;

    Ticket updateTicket(UUID ticketId, TicketUpdateDTO ticketRequest) throws EntityCreationException;

    void deleteTicket(UUID ticketId) throws EntityCreationException;

    List<TicketCount> getTicketCountByStatus();
}
