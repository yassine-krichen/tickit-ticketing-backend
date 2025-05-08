package com.sofrecom.tickit.listener;

import com.sofrecom.tickit.model.Ticket;

/**
 * Event triggered when a ticket is resolved.
 */
public class TicketResolvedEvent extends TicketEvent {
    public TicketResolvedEvent(Ticket ticket) {
        super(ticket);
    }
}