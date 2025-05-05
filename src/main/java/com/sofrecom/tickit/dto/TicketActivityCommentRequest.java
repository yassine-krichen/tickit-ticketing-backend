package com.sofrecom.tickit.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for handling activity comment requests.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object containing details to create or update an activity comment")
public class TicketActivityCommentRequest {
    
    /**
     * Content of the comment.
     */
    @NotBlank(message = "Comment content is required")
    @Size(min = 1, max = 4000, message = "Comment must be between 1 and 4000 characters")
    @Schema(description = "Content of the comment", example = "This looks good, approved!", required = true)
    private String content;
    
    // Username will be automatically set from authentication context
    private String username;
}