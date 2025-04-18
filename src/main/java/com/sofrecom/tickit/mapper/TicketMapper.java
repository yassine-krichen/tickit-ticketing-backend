package com.sofrecom.tickit.mapper;

import com.sofrecom.tickit.dto.PaginatedResponse;
import com.sofrecom.tickit.dto.TicketRequest;
import com.sofrecom.tickit.dto.TicketResponse;
import com.sofrecom.tickit.model.Ticket;
import com.sofrecom.tickit.model.Acte_traitement;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper component for converting between Ticket entity and DTOs.
 */
@Component
public class TicketMapper {
    private static final Logger logger = LoggerFactory.getLogger(TicketMapper.class);

    /**
     * Converts a TicketRequest DTO to a Ticket entity.
     *
     * @param request the TicketRequest DTO.
     * @return the Ticket entity.
     */
    public Ticket toEntity(TicketRequest request) {
        try {
            logger.debug("Starting to map TicketRequest to Ticket entity");
            logger.debug("TicketRequest: {}", request);
            
            if (request == null) {
                logger.error("TicketRequest is null");
                throw new IllegalArgumentException("TicketRequest cannot be null");
            }
            
            if (request.getActeTraitement() == null) {
                logger.error("TicketRequest acteTraitement is null");
                throw new IllegalArgumentException("ActeTraitement cannot be null");
            }
            
            // Create a reference to the Acte_traitement instead of trying to load it
            Acte_traitement acteTraitement = new Acte_traitement();
            acteTraitement.setIdacte(request.getActeTraitement());
            
            Ticket ticket = Ticket.builder()
                    .acteTraitement(acteTraitement)
                    .subject(request.getSubject())
                    .description(request.getDescription())
                    .type(request.getType())
                    .step(request.getStep())
                    .subStatus(request.getSubStatus())
                    .priority(request.getPriority())
                    .status(request.getStatus())
                    .issuedBy(request.getIssuedBy())
                    .build();
                    
            logger.debug("Successfully mapped to Ticket: {}", ticket);
            return ticket;
        } catch (Exception e) {
            logger.error("Error mapping TicketRequest to Ticket entity", e);
            throw e;
        }
    }

    /**
     * Maps a Ticket entity to a TicketResponse DTO.
     *
     * @param ticket The Ticket entity.
     * @return The corresponding TicketResponse DTO.
     */
    public TicketResponse toResponse(Ticket ticket) {
        if (ticket == null) {
            return null;
        }
        return TicketResponse.builder()
                .id(ticket.getId())
                .acteTraitement(ticket.getActeTraitement().getIdacte()) // Assuming toString() gives the desired representation
                .subject(ticket.getSubject())
                .description(ticket.getDescription())
                .type(ticket.getType())
                .step(ticket.getStep())
                .subStatus(ticket.getSubStatus())
                .priority(ticket.getPriority())
                .status(ticket.getStatus())
                .issuedBy(ticket.getIssuedBy())
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .responseSla(ticket.getResponseSla())
                .resolutionSla(ticket.getResolutionSla())
                // statusMessage and message are typically set in the controller/service
                .build();
    }

    /**
     * Maps a Page of Ticket entities to a PaginatedResponse containing TicketResponse DTOs.
     *
     * @param ticketPage The Page<Ticket> object from the repository.
     * @return A PaginatedResponse<TicketResponse>.
     */
    public PaginatedResponse<TicketResponse> toPaginatedResponse(Page<Ticket> ticketPage) {
        List<TicketResponse> ticketResponses = ticketPage.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return PaginatedResponse.fromPage(ticketPage, ticketResponses);
    }
}
