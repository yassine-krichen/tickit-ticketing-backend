package com.sofrecom.tickit.mapper;

import com.sofrecom.tickit.dto.TicketActivityCommentResponse;
import com.sofrecom.tickit.model.TicketActivityComment;

import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between TicketActivityComment entities and DTOs.
 */
@Component
public class TicketActivityCommentMapper {

    /**
     * Converts a TicketActivityComment entity to a TicketActivityCommentResponse DTO.
     *
     * @param comment The comment entity
     * @return The comment response DTO
     */
    public TicketActivityCommentResponse toResponse(TicketActivityComment comment) {
        if (comment == null) {
            return null;
        }
        
        return TicketActivityCommentResponse.builder()
                .id(comment.getId())
                .activityId(comment.getActivity().getId())
                .content(comment.getContent())
                .username(comment.getUsername())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}