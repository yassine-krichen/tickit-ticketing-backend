package com.sofrecom.tickit.listener;

import com.sofrecom.tickit.model.Ticket;
import lombok.Getter;

/**
 * Event triggered when a ticket's priority is escalated.
 */
@Getter
public class TicketPriorityEscalatedEvent extends TicketEvent {
    private final Ticket.Priority oldPriority;
    private final Ticket.Priority newPriority;
    
    /**
     * Creates a new priority escalation event.
     *
     * @param ticket The ticket that had its priority escalated
     * @param oldPriority The previous priority
     * @param newPriority The new priority
     */
    public TicketPriorityEscalatedEvent(Ticket ticket, Ticket.Priority oldPriority, Ticket.Priority newPriority) {
        super(ticket);
        this.oldPriority = oldPriority;
        this.newPriority = newPriority;
    }
}