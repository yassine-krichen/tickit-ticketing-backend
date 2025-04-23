package com.sofrecom.tickit.dto;

import com.sofrecom.tickit.model.TicketActivity.ActivityType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) for handling ticket activity requests.
 * This class is used to encapsulate the data required to create and update a ticket activity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object containing details to create or update a ticket activity")
public class TicketActivityRequest {

    /**
     * The username of the person performing the activity.
     */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Schema(description = "Username of the person performing the activity", example = "john.doe", required = true)
    private String username;

    /**
     * The type of activity being performed (e.g., CREATE, UPDATE, DELETE).
     */
    @NotNull(message = "Activity type is required")
    @Schema(description = "Type of activity being performed", 
            example = "UPDATE", 
            required = true, 
            allowableValues = {"OPEN", "CLOSE", "UPDATE", "EMAIL", "CALL", "RESEARCH", "ASSIGNMENT", "OTHER"})
    private ActivityType type;

    /**
     * A description of the activity being performed.
     */
    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    @Schema(description = "Detailed description of the activity being performed", 
            example = "Updated ticket priority from LOW to HIGH", 
            required = false, 
            maxLength = 2000)
    private String description;
}
