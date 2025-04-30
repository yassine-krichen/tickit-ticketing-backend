package com.sofrecom.tickit.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a user entity in the system.
 */
@Entity
@Table(name = "users", uniqueConstraints = { 
	@UniqueConstraint(columnNames = "username"),
	@UniqueConstraint(columnNames = "email") 
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

	/**
	 * The unique identifier for the user.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * The username of the user.
	 */
	@NotBlank
	@Size(max = 20)
	private String username;

	/**
	 * The email address of the user.
	 */
	@NotBlank
	@Size(max = 50)
	@Email
	private String email;

	/**
	 * The password of the user.
	 */
	@NotBlank
	@Size(max = 120)
	private String password;

	/**
	 * The roles assigned to the user.
	 */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
	name = "user_roles", 
	joinColumns = @JoinColumn(name = "user_id"), 
	inverseJoinColumns = @JoinColumn(name = "role_id")
	)
	private Set<Role> roles = new HashSet<>();

	/**
	 * Indicates whether the user is active.
	 */
	private boolean active = false;

	/**
	 * Constructor with username, email, password, and active status.
	 *
	 * @param username The username of the user.
	 * @param email The email address of the user.
	 * @param password The password of the user.
	 * @param active Indicates whether the user is active.
	 */
	public User(String username, String email, String password, boolean active) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.active = active;
	}
}
