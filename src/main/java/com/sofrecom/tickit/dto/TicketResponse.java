package com.sofrecom.tickit.dto;

import com.sofrecom.tickit.model.CaseType;
import com.sofrecom.tickit.model.Ticket;
import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for sending ticket details in API responses.
 */
@Data
@Builder
public class TicketResponse {

    /** Unique identifier for the ticket. */
    private UUID id;

    /** Identifier for the related processing act or workflow. */
    private String acteTraitement;

    /** Brief summary or title of the ticket. */
    private String subject;

    /** Detailed description of the issue or request. */
    private String description;

    /** The general category or type of the ticket. */
    private CaseType type;

    /** The current step or stage in the ticket's lifecycle. */
    private CaseType step;

    /** A more granular status indicator. */
    private String subStatus;

    /** The priority level of the ticket. */
    private Ticket.Priority priority;

    /** The current status of the ticket. */
    private Ticket.Status status;

    /** Identifier of the user or system that created the ticket. */
    private String issuedBy;

    /** Timestamp when the ticket was created. */
    private LocalDateTime createdAt;

    /** Timestamp when the ticket was last updated. */
    private LocalDateTime updatedAt;

    /** Service Level Agreement (SLA) for the initial response time. */
    private Duration responseSla;

    /** Service Level Agreement (SLA) for the final resolution time. */
    private Duration resolutionSla;

    /** User-friendly status message (e.g., "SUCCESS", "FAILURE"). */
    private String statusMessage;

    /** Additional user-friendly message (e.g., "Ticket retrieved successfully"). */
    private String message;
}
