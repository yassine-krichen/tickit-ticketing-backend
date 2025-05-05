package com.sofrecom.tickit.mapper;

import com.sofrecom.tickit.dto.AttachmentResponse;
import com.sofrecom.tickit.model.Attachment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between Attachment entities and DTOs.
 */
@Component
public class AttachmentMapper {
    
    /**
     * Converts an Attachment entity to an AttachmentResponse DTO.
     *
     * @param attachment The attachment entity
     * @return The attachment response DTO
     */
    public AttachmentResponse toResponse(Attachment attachment) {
        if (attachment == null) {
            return null;
        }
        
        return AttachmentResponse.builder()
                .id(attachment.getId())
                .activityId(attachment.getActivity().getId())
                .filename(attachment.getFilename())
                .contentType(attachment.getContentType())
                .size(attachment.getSize())
                .createdAt(attachment.getCreatedAt())
                .updatedAt(attachment.getUpdatedAt())
                .build();
    }
    
    /**
     * Converts a list of Attachment entities to a list of AttachmentResponse DTOs.
     *
     * @param attachments The list of attachment entities
     * @return The list of attachment response DTOs
     */
    public List<AttachmentResponse> toResponseList(List<Attachment> attachments) {
        return attachments.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}