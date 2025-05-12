package com.sofrecom.tickit.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.sofrecom.tickit.impl.UserDetailsImpl;
import com.sofrecom.tickit.model.Collaborateur;
import com.sofrecom.tickit.model.Equipe;
import com.sofrecom.tickit.repository.CollaborateurRepository;

/**
 * Utility class for authentication-related operations.
 * Provides methods to extract user information from the security context.
 */
@Component
public class AuthUtils {

    @Autowired
    private CollaborateurRepository collaborateurRepository;


    /**
     * Gets the username of the currently authenticated user.
     * 
     * @return The username of the authenticated user, or "system" if no user is authenticated
     */
    public String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return "system";
        }
        
        if (authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Collaborateur collaborateur = collaborateurRepository.findByCUID(userDetails.getUsername())
                    .orElse(null);
            if (collaborateur != null) {
                String firstName = collaborateur.getPrenom() != null ? collaborateur.getPrenom() : "unknown";
                String lastName = collaborateur.getNom() != null ? collaborateur.getNom() : "unknown";
                return String.format("%s %s", firstName, lastName);
            }

            return "unknown_user";
        }
        
        // Fallback to using principal's toString if it's not a UserDetailsImpl
        return authentication.getName();
    }


    /**
     * Generates a user fingerprint in the format last_name.first_name.role.team
     * based on the currently authenticated user.
     *
     * @return Formatted user fingerprint string
     */
    public String generateUserFingerprint() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // Handle unauthenticated scenarios
        if (authentication == null || !authentication.isAuthenticated()) {
            return "system.anonymous.guest.system";
        }
        
        // Handle cases where principal isn't UserDetailsImpl
        if (!(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            return "system.user.unknown.system";
        }

        
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Collaborateur collaborateur = collaborateurRepository.findByCUID(userDetails.getUsername())
                .orElse(null);
        
        // Handle missing collaborateur data
        if (collaborateur == null) {
            return String.format("system.%s.user.system", 
                    sanitizeValue(userDetails.getUsername()));
        }
        
        // Extract required components
        String lastName = sanitizeValue(collaborateur.getNom());
        String firstName = sanitizeValue(collaborateur.getPrenom());
        
        // Get primary role (first role without ROLE_ prefix)
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(r -> r.replace("ROLE_", "").toLowerCase())
                .findFirst()
                .orElse("user");
        
        // Extract team name
        String team = "unknown";
        Equipe equipe = collaborateur.getEquipe();
        if (equipe != null && equipe.getNom() != null) {
            team = sanitizeValue(equipe.getNom());
        }
        
        return String.format("%s.%s.%s.%s", lastName, firstName, role, team);
    }
    
    /**
     * Sanitizes a value to ensure it conforms to the fingerprint format requirements.
     * 
     * @param value The input value to sanitize
     * @return Sanitized value that contains only lowercase letters with no spaces
     */
    private String sanitizeValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "unknown";
        }
        
        // Convert to lowercase, replace spaces and special chars with underscores
        return value.toLowerCase()
                .replaceAll("[^a-z0-9]", "_") // Replace non-alphanumeric with underscore
                .replaceAll("_+", "_")        // Replace multiple underscores with single
                .replaceAll("^_|_$", "");     // Remove leading/trailing underscores
    }

    //get cuid
    /**
     * Retrieves the CUID (Collaborateur Unique ID) of the currently authenticated user.
     * 
     * @return The CUID of the authenticated user, or "system" if no user is authenticated
     */
    public String getCUID() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "system";
        }
        if (authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            return userDetails.getUsername();
        }
        return "Something Weird is going on";
    }
}