// UserService.java
package com.sofrecom.tickit.service;

import com.sofrecom.tickit.dto.UserInfoDTO;
import com.sofrecom.tickit.dto.UserResponseDTO;

public interface UserService {
    /**
     * Fetches data for the currently authenticated user.
     *
     * @return non-null UserResponseDTO
     * @throws UserNotFoundException if user record is missing
     * @throws AuthenticationCredentialsNotFoundException if no user is authenticated
     */
    UserResponseDTO getCurrentUser();

    /**
     * Gets information about the current user including full name and roles.
     *
     * @return UserInfoDTO containing user's full name, all roles, and highest role
     * @throws UserNotFoundException if user record is missing
     * @throws AuthenticationCredentialsNotFoundException if no user is authenticated
     */
    UserInfoDTO getCurrentUserInfo();
}
