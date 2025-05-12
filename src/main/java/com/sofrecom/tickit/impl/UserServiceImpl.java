package com.sofrecom.tickit.impl;

import com.sofrecom.tickit.service.UserService;
import com.sofrecom.tickit.util.AuthUtils;

import lombok.RequiredArgsConstructor;
import com.sofrecom.tickit.repository.CollaborateurRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Service;

import com.sofrecom.tickit.dto.UserResponseDTO;
import com.sofrecom.tickit.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        com.sofrecom.tickit.model.Collaborateur collaborator = 
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
}