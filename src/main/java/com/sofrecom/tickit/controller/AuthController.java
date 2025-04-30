package com.sofrecom.tickit.controller;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.sofrecom.tickit.exception.ResourceNotFoundException;
import com.sofrecom.tickit.model.Equipe;
import com.sofrecom.tickit.repository.EquipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sofrecom.tickit.model.Role;
import com.sofrecom.tickit.model.User;
import com.sofrecom.tickit.dto.LoginRequest;
import com.sofrecom.tickit.dto.SignupRequest;
import com.sofrecom.tickit.dto.JwtResponse;
import com.sofrecom.tickit.dto.MessageResponse;
import com.sofrecom.tickit.dto.RoleAssignmentRequest;
import com.sofrecom.tickit.repository.RoleRepository;
import com.sofrecom.tickit.repository.UserRepository;
import com.sofrecom.tickit.security.jwt.JwtUtils;
import com.sofrecom.tickit.impl.UserDetailsImpl;
import com.sofrecom.tickit.model.Collaborateur;
import com.sofrecom.tickit.repository.CollaborateurRepository;

/**
 * Controller for handling authentication related endpoints.
 * Provides functionality for user login, registration and role management.
 */
// @CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final CollaborateurRepository collaborateurRepository;
    private final RoleRepository roleRepository;
    private final EquipeRepository equipeRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    /**
     * Constructor-based dependency injection.
     */
    @Autowired
    public AuthController(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            CollaborateurRepository collaborateurRepository,
            RoleRepository roleRepository,
            EquipeRepository equipeRepository,
            PasswordEncoder encoder,
            JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.collaborateurRepository = collaborateurRepository;
        this.roleRepository = roleRepository;
        this.equipeRepository = equipeRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
    }

    /**
     * Authenticates a user and returns a JWT token.
     *
     * @param loginRequest The login credentials
     * @return JWT token response with user details
     */
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        // Authenticate the user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        // Set authentication in security context
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // Generate JWT token
        String jwt = jwtUtils.generateJwtToken(authentication);

        // Get user details
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        // Extract roles
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        
        // Find collaborateur by username/CUID
        Collaborateur collaborateur = collaborateurRepository.findByCUID(userDetails.getUsername())
                .orElse(null); // Handle case where collaborateur might not exist
        
        // Return JWT response
        return ResponseEntity.ok(new JwtResponse(
                jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                userDetails.getActive(),
                roles,
                collaborateur));
    }

    /**
     * Registers a new user in the system.
     *
     * @param signUpRequest User registration details
     * @return Success or error message
     */
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        // Check if username already exists
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("cuidUtilise"));
        }

        // Check if email already exists
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("emailUtilise"));
        }

        // Find equipe by ID
        Equipe equipe = equipeRepository.findByidEquipe(signUpRequest.getIdEquipe())
                .orElseThrow(() -> new ResourceNotFoundException("Equipe" , "Id", signUpRequest.getIdEquipe()));

        // Create new user account
        User user = new User(
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()),
                false);

        // Create new collaborateur
        Collaborateur collaborateur = new Collaborateur(
                signUpRequest.getUsername(),
                signUpRequest.getNom(),
                signUpRequest.getPrenom(),
                equipe,
                new Date(),
                signUpRequest.getTelephone(),
                signUpRequest.getEmail());

        // Save both entities
        userRepository.save(user);
        collaborateurRepository.save(collaborateur);

        // Return success response
        return ResponseEntity.ok(new MessageResponse("inscriptionReussi"));
    }

    /**
     * Assigns roles to an existing user.
     *
     * @param cuid Username/CUID of the user
     * @param request Object containing list of role names to assign
     * @return Success or error message
     */
    @PostMapping("/assignRoles/{cuid}")
    public ResponseEntity<?> assignRolesToUser(
            @PathVariable String cuid, 
            @Valid @RequestBody RoleAssignmentRequest request) {
        // Find user by username
        User user = userRepository.findByUsername(cuid)
                .orElseThrow(() -> new ResourceNotFoundException("User", "CUID", cuid));

        // Create set of roles
        Set<Role> roles = new HashSet<>();
        
        // Find each role and add to set
        for (String roleName : request.getRoles()) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "Name", roleName));
            roles.add(role);
        }

        // Update user roles
        user.setRoles(roles);
        userRepository.save(user);

        // Return success response
        return ResponseEntity.ok(new MessageResponse("Roles assigned successfully"));
    }
}