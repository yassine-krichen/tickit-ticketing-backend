package com.sofrecom.tickit.listener;

import com.sofrecom.tickit.model.Ticket;
import lombok.Getter;

/**
 * Event triggered when an SLA breach occurs.
 */
@Getter
public class SlaBreachEvent extends TicketEvent {
    private final String slaType; // "response" or "resolution"
    
    /**
     * Creates a new SLA breach event.
     *
     * @param ticket The ticket with the SLA breach
     * @param slaType The type of SLA that was breached
     */
    public SlaBreachEvent(Ticket ticket, String slaType) {
        super(ticket);
        this.slaType = slaType;
    }
}