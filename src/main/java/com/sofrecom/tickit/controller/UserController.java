package com.sofrecom.tickit.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sofrecom.tickit.dto.UserResponseDTO;
import com.sofrecom.tickit.dto.UserInfoDTO;
import com.sofrecom.tickit.exception.UserNotFoundException;
import com.sofrecom.tickit.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getProfile() {
        try {
            UserResponseDTO dto = userService.getCurrentUser();
            return ResponseEntity.ok(dto);
        } catch (AuthenticationCredentialsNotFoundException ex) {
            UserResponseDTO err = UserResponseDTO.builder()
                .message("Unauthorized: " + ex.getMessage())
                .build();
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(err);
        } catch (UserNotFoundException ex) {
            UserResponseDTO err = UserResponseDTO.builder()
                .message("Not Found: " + ex.getMessage())
                .build();
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(err);
        } catch (Exception ex) {
            UserResponseDTO err = UserResponseDTO.builder()
                .message("Internal Server Error")
                .build();
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(err);
        }
    }

    /**
     * Retrieves detailed information about the current user including username and roles.
     *
     * @return ResponseEntity containing user information or appropriate error response
     */
    @GetMapping("/info")
    public ResponseEntity<UserInfoDTO> getUserInfo() {
        try {
            UserInfoDTO dto = userService.getCurrentUserInfo();
            return ResponseEntity.ok(dto);
        } catch (AuthenticationCredentialsNotFoundException ex) {
            UserInfoDTO err = UserInfoDTO.builder()
                    .message("Unauthorized: " + ex.getMessage())
                    .build();
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(err);
        } catch (UserNotFoundException ex) {
            UserInfoDTO err = UserInfoDTO.builder()
                    .message("Not Found: " + ex.getMessage())
                    .build();
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(err);
        } catch (Exception ex) {
            UserInfoDTO err = UserInfoDTO.builder()
                    .message("Internal Server Error: " + ex.getMessage())
                    .build();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(err);
        }
    }
}
