package com.sofrecom.tickit.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * DTO representing the login request sent by the client.
 */
@Data
@NoArgsConstructor
public class LoginRequest {

	/**
	 * The username of the user attempting to log in.
	 * This field is mandatory.
	 */
	@NotBlank
	private String username;

	/**
	 * The password of the user attempting to log in.
	 * This field is mandatory.
	 */
	@NotBlank
	private String password;
}
