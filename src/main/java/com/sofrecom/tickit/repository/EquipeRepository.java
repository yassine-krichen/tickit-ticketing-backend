package com.sofrecom.tickit.repository;

import com.sofrecom.tickit.model.Equipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Equipe entity operations.
 * Provides methods for CRUD operations and custom queries related to teams.
 */
@Repository
public interface EquipeRepository extends JpaRepository<Equipe, Integer> {
    
    /**
     * Finds a team by its ID.
     *
     * @param idEquipe the ID of the team to find
     * @return an Optional containing the team if found, empty otherwise
     */
    Optional<Equipe> findByidEquipe(int idEquipe);
    
    /**
     * Finds a team by its name.
     *
     * @param nomEquipe the name of the team to find
     * @return an Optional containing the team if found, empty otherwise
     */
    Optional<Equipe> findBynom(String nomEquipe);
    
    /**
     * Checks if a team with the specified name exists.
     *
     * @param nomEquipe the team name to check
     * @return true if a team with the given name exists, false otherwise
     */
    boolean existsBynom(String nomEquipe);
}