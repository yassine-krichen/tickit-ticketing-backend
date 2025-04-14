package com.sofrecom.tickit.service;

import com.sofrecom.tickit.dto.CaseRequest;
import com.sofrecom.tickit.dto.CaseResponse;
import com.sofrecom.tickit.dto.CaseUpdate;
import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing cases.
 */
public interface CaseService {

    /**
     * Creates a new Case.
     *
     * @param createCaseDTO the DTO with case creation details.
     * @return the created Case as a DTO.
     */
    CaseResponse createCase(CaseRequest createCaseDTO);

    /**
     * Retrieves a list of all cases.
     *
     * @return list of CaseResponse objects.
     */
    List<CaseResponse> getAllCases();

    /**
     * Retrieves a Case by its ID.
     *
     * @param id the case identifier.
     * @return the Case as a DTO.
     */
    CaseResponse getCaseById(UUID id);

    /**
     * Updates an existing Case.
     *
     * @param id the case identifier.
     * @param updateCaseDTO the DTO with updated details.
     * @return the updated Case as a DTO.
     */
    CaseResponse updateCase(UUID id, CaseUpdate updateCaseDTO);

    /**
     * Deletes a Case by its ID.
     *
     * @param id the case identifier.
     */
    void deleteCase(UUID id);
}
