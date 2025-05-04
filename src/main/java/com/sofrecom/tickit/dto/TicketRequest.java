package com.sofrecom.tickit.dto;

import com.sofrecom.tickit.model.CaseType;
import com.sofrecom.tickit.model.Ticket.Priority;
import com.sofrecom.tickit.model.Ticket.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketRequest {

    @NotBlank(message = "Acte de traitement is required")
    private String acteTraitement;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Description is required")
    private String description;
    
    @NotNull(message = "Type is required")
    private CaseType type;
    
    @NotNull(message = "Step is required")
    private CaseType step;
    
    private String subStatus;

    @NotNull(message = "Priority is required")
    private Priority priority;

    @NotNull(message = "Status is required")
    private Status status;

    private String issuedBy; // No longer required in request, will be populated from auth context

}

