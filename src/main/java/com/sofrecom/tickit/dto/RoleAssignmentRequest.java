package com.sofrecom.tickit.dto;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data transfer object for role assignment requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for assigning roles to a user")
public class RoleAssignmentRequest {
    
    /**
     * List of role names to assign to the user
     */
    @NotEmpty(message = "At least one role must be provided")
    @Schema(description = "List of role names to assign to the user", 
            example = "[\"ROLE_USER\", \"ROLE_ADMIN\"]", 
            required = true)
    private List<String> roles;
}