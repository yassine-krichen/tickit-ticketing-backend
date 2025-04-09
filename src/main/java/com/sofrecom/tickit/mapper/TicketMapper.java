package com.sofrecom.tickit.mapper;

import org.springframework.stereotype.Component;

import com.sofrecom.tickit.dto.TicketRequest;
import com.sofrecom.tickit.dto.TicketResponse;
import com.sofrecom.tickit.model.Ticket;

@Component
public class TicketMapper {
    /**
     * Converts a TicketRequest DTO to a Ticket entity.
     *
     * @param request the TicketRequest DTO.
     * @return the Ticket entity.
     */
    public Ticket toEntity(TicketRequest request) {
        return Ticket.builder()
                .acteTraitement(request.getActeTraitement())
                .subject(request.getSubject())
                .description(request.getDescription())
                .priority(request.getPriority())
                .status(request.getStatus())
                .issuedBy(request.getIssuedBy())
                .build();
    }

    /**
     * Converts a Ticket entity to a TicketResponse DTO.
     *
     * @param ticket the Ticket entity.
     * @return the TicketResponse DTO.
     */
    public TicketResponse toResponse(Ticket ticket) {
        return TicketResponse.builder()
                .id(ticket.getId())
                .acteTraitement(ticket.getActeTraitement())
                .subject(ticket.getSubject())
                .description(ticket.getDescription())
                .priority(ticket.getPriority())
                .status(ticket.getStatus())
                .issuedBy(ticket.getIssuedBy())
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .build();
    }
}
