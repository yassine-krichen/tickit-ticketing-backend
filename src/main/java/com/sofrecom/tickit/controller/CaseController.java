package com.sofrecom.tickit.controller;

import com.sofrecom.tickit.dto.CaseRequest;
import com.sofrecom.tickit.dto.CaseResponse;
import com.sofrecom.tickit.dto.CaseUpdate;
import com.sofrecom.tickit.service.CaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing Cases.
 */
@RestController
@RequestMapping("/api/v1/cases")
public class CaseController {

    private final CaseService caseService;

    public CaseController(CaseService caseService) {
        this.caseService = caseService;
    }

    /**
     * Create a new Case.
     *
     * @param createCaseDTO the DTO with case creation details.
     * @return the created Case DTO.
     */
    @PostMapping
    public ResponseEntity<CaseResponse> createCase(@RequestBody CaseRequest createCaseDTO) {
        CaseResponse createdCase = caseService.createCase(createCaseDTO);
        return ResponseEntity.status(201).body(createdCase);
    }

    /**
     * Retrieve all cases.
     *
     * @return list of CaseResponse objects.
     */
    @GetMapping
    public ResponseEntity<List<CaseResponse>> getAllCases() {
        List<CaseResponse> cases = caseService.getAllCases();
        return ResponseEntity.ok(cases);
    }

    /**
     * Retrieve a specific case by id.
     *
     * @param id the case identifier.
     * @return the Case DTO.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CaseResponse> getCaseById(@PathVariable UUID id) {
        CaseResponse caseDTO = caseService.getCaseById(id);
        return ResponseEntity.ok(caseDTO);
    }

    /**
     * Update an existing Case.
     *
     * @param id the case identifier.
     * @param updateCaseDTO the DTO with updated case details.
     * @return the updated Case DTO.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CaseResponse> updateCase(@PathVariable UUID id, @RequestBody CaseUpdate updateCaseDTO) {
        CaseResponse updatedCase = caseService.updateCase(id, updateCaseDTO);
        return ResponseEntity.ok(updatedCase);
    }

    /**
     * Delete a Case.
     *
     * @param id the case identifier.
     * @return a 204 No Content response.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCase(@PathVariable UUID id) {
        caseService.deleteCase(id);
        return ResponseEntity.noContent().build();
    }
}
