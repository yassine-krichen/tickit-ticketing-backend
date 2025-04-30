package com.sofrecom.tickit.impl;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.sofrecom.tickit.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Custom implementation of Spring Security's UserDetails interface.
 * Provides core user authentication information as required by Spring Security framework.
 * Acts as an adapter between our application's User model and Spring Security's expected user structure.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserDetailsImpl implements UserDetails {
    
    private static final long serialVersionUID = 1L;

    /**
     * Unique identifier for the user
     */
    private Long id;

    /**
     * Username for authentication
     */
    private String username;

    /**
     * User's email address
     */
    private String email;

    /**
     * Whether the user account is active
     */
    @Setter
    private Boolean active;

    /**
     * User's password (marked to be ignored during serialization for security)
     */
    @JsonIgnore
    private String password;

    /**
     * Authorities/roles granted to the user
     */
    private Collection<? extends GrantedAuthority> authorities;

    /**
     * Factory method to build UserDetailsImpl from User entity
     *
     * @param user The domain User entity
     * @return A new UserDetailsImpl instance with data from the User entity
     */
    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        return UserDetailsImpl.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .active(user.isActive())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }

    /**
     * Returns the list of role-based authorities granted to the user
     *
     * @return Collection of granted authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * Returns string representation of roles for this user
     * 
     * @return List of role names as strings
     */
    public List<String> getRoles() {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    /**
     * Indicates whether the user's account has expired (always returns true)
     *
     * @return true if the account is not expired
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is locked or unlocked (always returns true)
     *
     * @return true if the account is not locked
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the user's credentials have expired (always returns true)
     *
     * @return true if credentials are valid
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is enabled (always returns true)
     * Note: The actual active status is stored in the 'active' field
     *
     * @return true as the account is always enabled at the UserDetails level
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Compares this UserDetailsImpl with another object for equality.
     * Two UserDetailsImpl instances are considered equal if they have the same id.
     *
     * @param o The object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }

    /**
     * Generates a hash code for this UserDetailsImpl based on its id.
     *
     * @return The hash code value
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}