package com.sofrecom.tickit.dto;

import com.sofrecom.tickit.model.Ticket.Status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TicketCount {
    private Status status;
    private long count;
}
