package com.sofrecom.tickit.listener;

import com.sofrecom.tickit.model.Ticket;

/**
 * Event triggered when a ticket is updated.
 */
public class TicketUpdatedEvent extends TicketEvent {
    public TicketUpdatedEvent(Ticket ticket) {
        super(ticket);
    }
}