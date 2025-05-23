package com.sofrecom.tickit.impl;

import com.sofrecom.tickit.service.UserService;
import com.sofrecom.tickit.util.AuthUtils;

import lombok.RequiredArgsConstructor;
import com.sofrecom.tickit.repository.CollaborateurRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;

import com.sofrecom.tickit.dto.UserResponseDTO;
import com.sofrecom.tickit.dto.UserInfoDTO;
import com.sofrecom.tickit.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sofrecom.tickit.model.Collaborateur;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private final CollaborateurRepository collaborateurRepository;
    private final AuthUtils authUtils;

    @Override
    public UserResponseDTO getCurrentUser() {
        logger.info("getCurrentUser() called");

        String cuid = authUtils.getCUID();
        logger.debug("Authenticated username: {}", cuid);

        if ("system".equals(cuid)) {
            logger.warn("No authenticated user found");
            throw new AuthenticationCredentialsNotFoundException("No authenticated user");
        }

        Collaborateur collaborator = 
            collaborateurRepository.findByCUID(cuid)
                .orElseThrow(() -> {
                    logger.error("User not found for CUID: {}", cuid);
                    return new UserNotFoundException(cuid);
                });

        String avatar = collaborator.getPhoto() != null
            ? collaborator.getPhoto()
            : "No Image Available";

        logger.debug("Collaborator details - Email: {}, Avatar: {}", collaborator.getEmail(), avatar);

        UserResponseDTO response = UserResponseDTO.builder()
            .message("User Data Retrieved Successfully")
            .fingerprint(authUtils.generateUserFingerprint())
            .email(collaborator.getEmail())
            .avatarUrl(avatar)
            .build();

        logger.info("UserResponseDTO successfully built: {}", response);
        return response;
    }

    @Override
    public UserInfoDTO getCurrentUserInfo() {
        logger.info("getCurrentUserInfo() called");

        String cuid = authUtils.getCUID();
        logger.debug("Authenticated username (CUID): {}", cuid);

        if ("system".equals(cuid)) {
            logger.warn("No authenticated user found");
            throw new AuthenticationCredentialsNotFoundException("No authenticated user");
        }

        // Get the user's full name
        String fullName = authUtils.getAuthenticatedUsername();
        
        // Get current authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // Extract roles from authentication
        Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        
        // Determine highest role based on hierarchy
        String highestRole = determineHighestRole(roles);
        
        logger.debug("User roles: {}, highest role: {}", roles, highestRole);
        
        UserInfoDTO response = UserInfoDTO.builder()
                .username(fullName)
                .roles(roles)
                .highestRole(highestRole)
                .message("User information retrieved successfully")
                .build();
        
        logger.info("UserInfoDTO successfully built for user: {}", fullName);
        return response;
    }

    /**
     * Determines the highest ranking role from a set of roles.
     * Role hierarchy: ROLE_ADMIN > ROLE_DIRECTION > ROLE_USER > others
     *
     * @param roles Set of role names
     * @return The highest ranking role
     */
    private String determineHighestRole(Set<String> roles) {
        // Define role hierarchy (highest to lowest)
        final List<String> ROLE_HIERARCHY = Arrays.asList(
                "ROLE_ADMIN",
                "ROLE_DIRECTION", 
                "ROLE_PILOTE",
                "ROLE_PRODUCTEUR",
                "ROLE_USER"
        );
        
        // Find the highest role in the hierarchy that the user has
        return ROLE_HIERARCHY.stream()
                .filter(roles::contains)
                .findFirst()
                .orElse(roles.isEmpty() ? "ROLE_GUEST" : roles.iterator().next());
    }
}