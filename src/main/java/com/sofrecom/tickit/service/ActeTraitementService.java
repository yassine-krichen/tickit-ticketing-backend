package com.sofrecom.tickit.service;

import com.sofrecom.tickit.dto.ActeTraitementRequest;
import com.sofrecom.tickit.dto.ActeTraitementResponse;
import com.sofrecom.tickit.dto.ActeTraitementUpdateRequest;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing treatment acts.
 * Handles business logic for creating, updating, retrieving, and deleting treatment acts.
 */
public interface ActeTraitementService {
    
    /**
     * Create a new treatment act.
     * If caseId is provided, adds it to existing case; otherwise creates a new case.
     * Records the operation in the blockchain ledger.
     *
     * @param request Treatment act request
     * @return Created treatment act response
     */
    ActeTraitementResponse createActeTraitement(ActeTraitementRequest request);
    
    /**
     * Retrieve a treatment act by its ID.
     *
     * @param id Treatment act ID
     * @return Found treatment act or throws exception if not found
     */
    ActeTraitementResponse getActeTraitementById(String id);
    
    /**
     * Retrieve all treatment acts.
     *
     * @return List of all active treatment acts
     */
    List<ActeTraitementResponse> getAllActeTraitements();
    
    /**
     * Retrieve all treatment acts associated with a specific case.
     *
     * @param caseId Case identifier
     * @return List of treatment acts belonging to the case
     */
    List<ActeTraitementResponse> getActeTraitementsByCaseId(UUID caseId);
    
    /**
     * Update an existing treatment act.
     * Records the update operation in the blockchain ledger.
     *
     * @param id Treatment act ID
     * @param updateRequest Treatment act update request
     * @return Updated treatment act
     */
    ActeTraitementResponse updateActeTraitement(String id, ActeTraitementUpdateRequest updateRequest);
    
    /**
     * Soft delete a treatment act.
     * Records the delete operation in the blockchain ledger.
     *
     * @param id Treatment act ID
     */
    void deleteActeTraitement(String id);
    
    /**
     * Get the number of treatment acts (steps) in a case.
     *
     * @param caseId Case identifier
     * @return Number of treatment acts
     */
    int getNumberOfStepsByCaseId(UUID caseId);
}