package com.sofrecom.tickit.dto;

import java.util.Set;
import lombok.Builder;
import lombok.Data;

/**
 * Data Transfer Object containing user information including full name and roles.
 * Used for returning user identity and permission information to the client.
 */
@Data
@Builder
public class UserInfoDTO {
    /**
     * User's full name in "firstName lastName" format
     */
    private String username;
    
    /**
     * Complete set of roles assigned to the user
     */
    private Set<String> roles;
    
    /**
     * The highest ranking role in the user's permission set
     */
    private String highestRole;
    
    /**
     * Optional message for status information
     */
    private String message;
}