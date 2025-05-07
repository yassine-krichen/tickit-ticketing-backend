package com.sofrecom.tickit.repository;

import com.sofrecom.tickit.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    
    Boolean existsByUsername(String username);
    
    Boolean existsByEmail(String email);
    
    /**
     * Find users with roles matching the given role names.
     * 
     * @param roleNames The names of the roles to match
     * @return List of users having at least one of the specified roles
     */
    List<User> findDistinctByRoles_NameIn(Set<String> roleNames);
}
