package com.sofrecom.tickit.dto;

import com.sofrecom.tickit.impl.UserDetailsImpl;
import com.sofrecom.tickit.model.Collaborateur;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO representing user details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsdto {

    /**
     * The unique identifier of the user.
     */
    private Long id;

    /**
     * The username of the user.
     */
    private String username;

    /**
     * The email address of the user.
     */
    private String email;

    /**
     * The roles assigned to the user.
     */
    private List<String> roles;

    /**
     * The collaborator details associated with the user.
     */
    private Collaborateur collaborateur;

    /**
     * Converts a UserDetailsImpl entity and Collaborateur entity to a UserDetailsdto.
     *
     * @param userDetailsImpl The user details implementation entity.
     * @param collaborateur   The collaborator entity.
     * @return A UserDetailsdto object.
     */
    public static UserDetailsdto convertEntityToDto(UserDetailsImpl userDetailsImpl, Collaborateur collaborateur) {
        return UserDetailsdto.builder()
            .id(userDetailsImpl.getId())
            .username(userDetailsImpl.getUsername())
            .email(userDetailsImpl.getEmail())
            // .roles(userDetailsImpl.getRoles())
            .collaborateur(collaborateur)
            .build();
    }
}
