package com.sofrecom.tickit.service;

import com.sofrecom.tickit.model.Ticket;
import com.sofrecom.tickit.dto.TicketUpdateDTO;
import com.sofrecom.tickit.exception.TicketCreationException;

import java.util.List;
import java.util.UUID;

public interface TicketService {

    Ticket createTicket(Ticket ticket) throws TicketCreationException;

    Ticket getTicketById(UUID ticketId);

    List<Ticket> getAllTickets(Ticket.Status status, Ticket.Priority priority, String issuedBy, String acteTraitement);

    Ticket updateTicketStatus(UUID ticketId, Ticket.Status status) throws TicketCreationException;

    Ticket updateTicket(UUID ticketId, TicketUpdateDTO ticketRequest) throws TicketCreationException;

    void deleteTicket(UUID ticketId) throws TicketCreationException;
}
