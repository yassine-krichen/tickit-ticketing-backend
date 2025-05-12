package com.sofrecom.tickit.repository;

import com.sofrecom.tickit.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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

    /**
     * Find users who have worked on activities for a given ticket.
     * This implementation properly handles the username formatting in activities.
     * 
     * @param ticketId The ticket ID to look for activities
     * @return List of users who have activities on the ticket
     */
    @Query("SELECT DISTINCT u FROM User u " +
           "JOIN Collaborateur c ON c.CUID = u.username " +
           "JOIN TicketActivity a ON CONCAT(c.prenom, ' ', c.nom) = a.username " +
           "WHERE a.ticket.id = :ticketId AND a.deleted = false")
    List<User> findUsersByTicketActivities(@Param("ticketId") UUID ticketId);
}
