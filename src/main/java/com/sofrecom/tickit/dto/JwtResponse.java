package com.sofrecom.tickit.dto;

import com.sofrecom.tickit.model.Collaborateur;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO representing the JWT response sent to the client after successful authentication.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {

	/**
	 * Constructor to initialize JwtResponse with specific fields.
	 *
	 * @param token        The JWT access token.
	 * @param id           The ID of the authenticated user.
	 * @param username     The username of the authenticated user.
	 * @param email        The email of the authenticated user.
	 * @param active       Indicates whether the user is active.
	 * @param roles        The roles assigned to the authenticated user.
	 * @param collaborateur The associated collaborator details.
	 */
	public JwtResponse(String token, Long id, String username, String email, Boolean active, List<String> roles, Collaborateur collaborateur) {
		this.token = token;
		this.id = id;
		this.username = username;
		this.email = email;
		this.active = active;
		this.roles = roles;
		this.collaborateur = collaborateur;
	}

	/**
	 * The JWT access token.
	 */
	private String token;

	/**
	 * The type of the token, default is "Bearer".
	 */
	private String type = "Bearer";

	/**
	 * The ID of the authenticated user.
	 */
	private Long id;

	/**
	 * The username of the authenticated user.
	 */
	private String username;

	/**
	 * The email of the authenticated user.
	 */
	private String email;

	/**
	 * Indicates whether the user is active.
	 */
	private Boolean active;

	/**
	 * The roles assigned to the authenticated user.
	 */
	private List<String> roles;

	/**
	 * The associated collaborator details.
	 */
	private Collaborateur collaborateur;
}
