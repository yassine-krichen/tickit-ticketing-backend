package com.sofrecom.tickit.repository;

import com.sofrecom.tickit.model.Collaborateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Collaborateur entity operations.
 * Provides methods for CRUD operations and custom queries related to collaborateurs.
 */
@Repository
public interface CollaborateurRepository extends JpaRepository<Collaborateur, Long> {
    
    /**
     * Finds a collaborateur by their CUID (Collaborator Unique ID).
     *
     * @param cuid the unique identifier of the collaborateur
     * @return an Optional containing the collaborateur if found, empty otherwise
     */
    Optional<Collaborateur> findByCUID(String cuid);
    
    /**
     * Checks if a collaborateur with the specified CUID exists.
     *
     * @param cuid the unique identifier to check
     * @return true if a collaborateur with the given CUID exists, false otherwise
     */
    boolean existsByCUID(String cuid);
    
    /**
     * Finds collaborateurs by email address.
     *
     * @param email the email address to search for
     * @return an Optional containing the collaborateur if found, empty otherwise
     */
    Optional<Collaborateur> findByEmail(String email);
    
    /**
     * Finds all collaborateurs belonging to a specific team.
     *
     * @param equipeId the ID of the team
     * @return a list of collaborateurs in the specified team
     */
    // List<Collaborateur> findByEquipeId(Long equipeId);

    /**
     * Finds collaborateurs by the ID of their team.
     *
     * @param idEquipe the ID of the team
     * @return a list of collaborateurs in the specified team
     */
    List<Collaborateur> findByEquipeIdEquipe(int idEquipe);

    /**
     * Finds a collaborateur by their name.
     *
     * @param nom the name of the collaborateur
     * @return an Optional containing the collaborateur if found, empty otherwise
     */
    Optional<Collaborateur> findByNom(String nom);
}