package com.sofrecom.tickit.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sofrecom.tickit.model.Role;

/**
 * Repository interface for managing {@link Role} entities.
 * Extends {@link JpaRepository} to provide CRUD operations and custom queries.
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

	// Optional<Role> findByName(ERole name);

	/**
	 * Finds a role by its name.
	 *
	 * @param roleName the name of the role
	 * @return an {@link Optional} containing the role if found, or empty otherwise
	 */
	Optional<Role> findByName(String roleName);
}
