package com.sofrecom.tickit.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sofrecom.tickit.model.User;
import com.sofrecom.tickit.repository.UserRepository;

/**
 * Implementation of Spring Security's UserDetailsService interface.
 * This service is responsible for loading user-specific data during authentication.
 * It retrieves user details from the database and adapts them to Spring Security's UserDetails structure.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	/**
	 * Loads a user by their username (CUID) from the database.
	 *
	 * @param username The username (CUID) of the user to be loaded.
	 * @return A UserDetails object containing user authentication information.
	 * @throws UsernameNotFoundException If no user is found with the given username.
	 */
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// Retrieve the user from the database or throw an exception if not found
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found with CUID: " + username));

		// Build and return a UserDetailsImpl object based on the retrieved user
		return UserDetailsImpl.build(user);
	}
}