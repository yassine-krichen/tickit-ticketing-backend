package com.sofrecom.tickit.dto;


import com.sofrecom.tickit.model.CaseType;
import com.sofrecom.tickit.model.Ticket.Priority;
import com.sofrecom.tickit.model.Ticket.Status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketUpdateDTO {
    private String subject;
    private String description;
    private CaseType type;
    private CaseType step;
    private String subStatus;
    private Priority priority;
    private Status status;
    private String issuedBy;

}
