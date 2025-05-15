package com.sofrecom.tickit.listener;

import com.sofrecom.tickit.model.Ticket;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Base class for ticket-related events.
 * Extends ApplicationEvent to be compatible with Spring's event system.
 */
@Getter
public abstract class TicketEvent extends ApplicationEvent {
    
    protected final Ticket ticket;
    
    /**
     * Creates a new ticket event.
     *
     * @param ticket The ticket associated with this event
     */
    public TicketEvent(Ticket ticket) {
        super(ticket); // The source of the event is the ticket
        this.ticket = ticket;
    }
}