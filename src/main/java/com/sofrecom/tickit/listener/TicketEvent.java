package com.sofrecom.tickit.listener;

import com.sofrecom.tickit.model.Ticket;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Base class for ticket-related events.
 */
@Getter
@RequiredArgsConstructor
public abstract class TicketEvent {
    protected final Ticket ticket;
}

// public class TicketCreatedEvent extends TicketEvent {
//     public TicketCreatedEvent(Ticket ticket) {
//         super(ticket);
//     }
// }

// public class TicketResolvedEvent extends TicketEvent {
//     public TicketResolvedEvent(Ticket ticket) {
//         super(ticket);
//     }
// }

// public class TicketUpdatedEvent extends TicketEvent {
//     public TicketUpdatedEvent(Ticket ticket) {
//         super(ticket);
//     }
// }

// @Getter
// public class TicketPriorityEscalatedEvent extends TicketEvent {
//     private final Ticket.Priority oldPriority;
//     private final Ticket.Priority newPriority;
    
//     public TicketPriorityEscalatedEvent(Ticket ticket, Ticket.Priority oldPriority, Ticket.Priority newPriority) {
//         super(ticket);
//         this.oldPriority = oldPriority;
//         this.newPriority = newPriority;
//     }
// }

// @Getter
// public class SlaBreachEvent extends TicketEvent {
//     private final String slaType; // "response" or "resolution"
    
//     public SlaBreachEvent(Ticket ticket, String slaType) {
//         super(ticket);
//         this.slaType = slaType;
//     }
// }

// @Getter
// @RequiredArgsConstructor
// public class ActivityStalledEvent {
//     private final UUID ticketId;
//     private final Long activityId;
//     private final String activityType;
//     private final String username;
//     private final int daysSinceUpdate;
// }

// @Getter
// @RequiredArgsConstructor
// public class CaseBlockedEvent {
//     private final UUID caseId;
//     private final String caseName;
//     private final UUID ticketId; // Can be null if not triggered by a ticket
// }

// @Getter
// @RequiredArgsConstructor
// public class DueDateApproachingEvent {
//     private final UUID caseId;
//     private final String caseName;
//     private final String acteTraitementId;
//     private final int daysRemaining;
// }