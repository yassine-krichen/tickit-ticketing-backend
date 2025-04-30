package com.sofrecom.tickit.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sofrecom.tickit.model.User;

/**
 * Repository interface for managing {@link User} entities.
 * Extends {@link JpaRepository} to provide CRUD operations and custom queries.
 */
public interface UserRepository extends JpaRepository<User, Long> {

	/**
	 * Finds a user by their username.
	 *
	 * @param username the username of the user
	 * @return an {@link Optional} containing the user if found, or empty otherwise
	 */
	Optional<User> findByUsername(String username);

	/**
	 * Checks if a user exists with the given username.
	 *
	 * @param username the username to check
	 * @return {@code true} if a user with the given username exists, {@code false} otherwise
	 */
	Boolean existsByUsername(String username);

	/**
	 * Checks if a user exists with the given email.
	 *
	 * @param email the email to check
	 * @return {@code true} if a user with the given email exists, {@code false} otherwise
	 */
	Boolean existsByEmail(String email);

	/**
	 * Finds users who have roles matching any of the given role names (case-insensitive).
	 *
	 * @param names a collection of role names to search for
	 * @return a list of users with matching roles
	 */
	List<User> findByRoles_NameInIgnoreCase(Collection<String> names);
}
