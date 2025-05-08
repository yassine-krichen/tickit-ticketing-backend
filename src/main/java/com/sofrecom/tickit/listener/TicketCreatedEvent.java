package com.sofrecom.tickit.listener;

import com.sofrecom.tickit.model.Ticket;

/**
 * Event triggered when a new ticket is created.
 */
public class TicketCreatedEvent extends TicketEvent {
    public TicketCreatedEvent(Ticket ticket) {
        super(ticket);
    }
}