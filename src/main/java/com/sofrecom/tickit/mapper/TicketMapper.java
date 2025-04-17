package com.sofrecom.tickit.mapper;

import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sofrecom.tickit.dto.TicketRequest;
import com.sofrecom.tickit.dto.TicketResponse;
import com.sofrecom.tickit.model.Ticket;
import com.sofrecom.tickit.model.Acte_traitement;

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
     * Converts a Ticket entity to a TicketResponse DTO.
     *
     * @param ticket the Ticket entity.
     * @return the TicketResponse DTO.
     */
    public TicketResponse toResponse(Ticket ticket) {
        try {
            logger.debug("Converting Ticket to TicketResponse");
            
            if (ticket == null) {
                logger.error("Ticket is null");
                throw new IllegalArgumentException("Ticket cannot be null");
            }
            
            if (ticket.getActeTraitement() == null) {
                logger.error("Ticket acteTraitement is null");
                throw new IllegalArgumentException("Ticket.acteTraitement cannot be null");
            }
            
            TicketResponse response = TicketResponse.builder()
                    .id(ticket.getId())
                    .acteTraitement(ticket.getActeTraitement().getIdacte())
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
                    .build();
                    
            logger.debug("Successfully mapped to TicketResponse: {}", response);
            return response;
        } catch (Exception e) {
            logger.error("Error mapping Ticket to TicketResponse", e);
            throw e;
        }
    }
}
