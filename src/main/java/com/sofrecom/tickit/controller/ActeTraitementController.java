package com.sofrecom.tickit.controller;

import com.sofrecom.tickit.dto.ActeTraitementRequest;
import com.sofrecom.tickit.dto.ActeTraitementResponse;
import com.sofrecom.tickit.dto.ActeTraitementUpdateRequest;
import com.sofrecom.tickit.service.ActeTraitementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST controller for managing treatment acts.
 * Provides endpoints for creating, retrieving, updating, and deleting treatment acts.
 */
@RestController
@RequestMapping("/api/acte-traitements")
@RequiredArgsConstructor
public class ActeTraitementController {

    private final ActeTraitementService acteTraitementService;

    /**
     * Create a new treatment act.
     * POST /api/acte-traitements
     *
     * @param request Treatment act request
     * @return ResponseEntity containing the created treatment act
     */
    @PostMapping
    public ResponseEntity<ActeTraitementResponse> createActeTraitement(@RequestBody ActeTraitementRequest request) {
        ActeTraitementResponse createdActe = acteTraitementService.createActeTraitement(request);
        return new ResponseEntity<>(createdActe, HttpStatus.CREATED);
    }

    /**
     * Retrieve a treatment act by its ID.
     * GET /api/acte-traitements/{id}
     *
     * @param id Treatment act ID
     * @return ResponseEntity containing the found treatment act
     */
    @GetMapping("/{id}")
    public ResponseEntity<ActeTraitementResponse> getActeTraitement(@PathVariable String id) {
        ActeTraitementResponse acteTraitement = acteTraitementService.getActeTraitementById(id);
        return ResponseEntity.ok(acteTraitement);
    }

    /**
     * Retrieve all treatment acts.
     * GET /api/acte-traitements
     *
     * @return ResponseEntity containing the list of all treatment acts
     */
    @GetMapping
    public ResponseEntity<List<ActeTraitementResponse>> getAllActeTraitements() {
        List<ActeTraitementResponse> acteTraitements = acteTraitementService.getAllActeTraitements();
        return ResponseEntity.ok(acteTraitements);
    }

    /**
     * Retrieve all treatment acts associated with a specific case.
     * GET /api/acte-traitements/case/{caseId}
     *
     * @param caseId Case identifier as string
     * @return ResponseEntity containing the list of treatment acts belonging to the case
     */
    @GetMapping("/case/{caseId}")
    public ResponseEntity<List<ActeTraitementResponse>> getActeTraitementsByCaseId(@PathVariable String caseId) {
        try {
            // Convert string to UUID
            UUID caseUuid = UUID.fromString(caseId);
            
            // Process with UUID
            List<ActeTraitementResponse> acteTraitements = acteTraitementService.getActeTraitementsByCaseId(caseUuid);
            return ResponseEntity.ok(acteTraitements);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format: " + caseId, e);
        }
    }

    /**
     * Get the number of treatment acts (steps) in a case.
     * GET /api/acte-traitements/case/{caseId}/steps
     *
     * @param caseId Case identifier
     * @return ResponseEntity containing the number of steps
     */
    @GetMapping("/case/{caseId}/steps")
    public ResponseEntity<Map<String, Integer>> getNumberOfStepsByCaseId(@PathVariable UUID caseId) {
        int steps = acteTraitementService.getNumberOfStepsByCaseId(caseId);
        return ResponseEntity.ok(Collections.singletonMap("steps", steps));
    }

    /**
     * Update an existing treatment act.
     * PUT /api/acte-traitements/{id}
     *
     * @param id Treatment act ID
     * @param updateRequest Treatment act update request
     * @return ResponseEntity containing the updated treatment act
     */
    @PutMapping("/{id}")
    public ResponseEntity<ActeTraitementResponse> updateActeTraitement(
            @PathVariable String id, 
            @RequestBody ActeTraitementUpdateRequest updateRequest) {
        ActeTraitementResponse updatedActe = acteTraitementService.updateActeTraitement(id, updateRequest);
        return ResponseEntity.ok(updatedActe);
    }

    /**
     * Delete a treatment act.
     * DELETE /api/acte-traitements/{id}
     *
     * @param id Treatment act ID
     * @return ResponseEntity with no content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActeTraitement(@PathVariable String id) {
        acteTraitementService.deleteActeTraitement(id);
        return ResponseEntity.noContent().build();
    }
}