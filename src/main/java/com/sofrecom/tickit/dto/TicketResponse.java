package com.sofrecom.tickit.dto;

import com.sofrecom.tickit.model.CaseType;
import com.sofrecom.tickit.model.Ticket.Priority;
import com.sofrecom.tickit.model.Ticket.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponse {

    private UUID id;
    private String acteTraitement;
    private String subject;
    private String description;
    private CaseType type;
    private CaseType step;
    private String subStatus;
    private Priority priority;
    private Status status;
    private String issuedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Duration responseSla;
    private Duration resolutionSla;

    // Additional fields for friendly response
    private String statusMessage;
    private String message;
}
