package com.sofrecom.tickit.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * DTO representing a signup request from the client.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {

    /**
     * The username of the user.
     * Must be between 3 and 20 characters.
     */
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    /**
     * The last name (nom) of the user.
     * Must be between 3 and 20 characters.
     */
    @NotBlank
    @Size(min = 3, max = 20)
    private String nom;

    /**
     * The first name (prenom) of the user.
     * Must be between 3 and 20 characters.
     */
    @NotBlank
    @Size(min = 3, max = 20)
    private String prenom;

    /**
     * The ID of the team (equipe) the user belongs to.
     */
    private int idEquipe;

    /**
     * The email address of the user.
     * Must be a valid email format and up to 50 characters.
     */
    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    /**
     * The password of the user.
     * Must be between 6 and 40 characters.
     */
    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    /**
     * The telephone number of the user.
     */
    private int telephone;
}
