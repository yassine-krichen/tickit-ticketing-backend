package com.sofrecom.tickit.repository;

import com.sofrecom.tickit.model.Acte_traitement;
import com.sofrecom.tickit.model.Case;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for {@link Acte_traitement} entities.
 * Provides methods to interact with the treatment acts data store.
 */
@Repository
public interface ActeTraitementRepository extends JpaRepository<Acte_traitement, String> {
    
    /**
     * Find all treatment acts associated with a specific case.
     *
     * @param caseId The UUID of the case
     * @return List of treatment acts belonging to the case
     */
    @Query("SELECT a FROM Acte_traitement a WHERE a.associatedCase.id = :caseId AND a.isDeleted = false")
    List<Acte_traitement> findByCaseId(@Param("caseId") UUID caseId);
    
    /**
     * Find all active (non-deleted) treatment acts.
     *
     * @return List of all active treatment acts
     */
    @Query("SELECT a FROM Acte_traitement a WHERE a.isDeleted = false")
    List<Acte_traitement> findAllActive();
    
    /**
     * Find an active treatment act by its ID.
     *
     * @param id The ID of the treatment act
     * @return Optional containing the treatment act if found
     */
    @Query("SELECT a FROM Acte_traitement a WHERE a.idacte = :id AND a.isDeleted = false")
    Optional<Acte_traitement> findActiveById(@Param("id") String id);
    
    /**
     * Find treatment acts by "prestation" type.
     *
     * @param prestationId The ID of the prestation
     * @return List of treatment acts with the specified prestation
     */
    @Query("SELECT a FROM Acte_traitement a WHERE a.typeprestation.id = :prestationId AND a.isDeleted = false")
    List<Acte_traitement> findByPrestationId(@Param("prestationId") String prestationId);
}