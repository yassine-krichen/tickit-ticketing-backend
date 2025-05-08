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

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Autowired
    private final CollaborateurRepository collaborateurRepository;
    private final AuthUtils authUtils;

    @Override
    public UserResponseDTO getCurrentUser() {
        String cuid = authUtils.getAuthenticatedUsername();
        if ("system".equals(cuid)) {
            throw new AuthenticationCredentialsNotFoundException("No authenticated user");
        }

        com.sofrecom.tickit.model.Collaborateur collaborator = 
            collaborateurRepository.findByCUID(cuid)
                .orElseThrow(() -> new UserNotFoundException(cuid));

        String avatar = collaborator.getPhoto() != null
            ? collaborator.getPhoto()
            : "No Image Available";

        return UserResponseDTO.builder()
            .message("User Data Retrieved Successfully")
            .fingerprint(authUtils.generateUserFingerprint())
            .email(collaborator.getEmail())
            .avatarUrl(avatar)
            .build();
    }
}