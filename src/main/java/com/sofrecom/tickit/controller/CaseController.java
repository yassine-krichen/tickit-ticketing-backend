package com.sofrecom.tickit.controller;

import com.sofrecom.tickit.dto.CaseRequest;
import com.sofrecom.tickit.dto.CaseResponse;
import com.sofrecom.tickit.dto.CaseUpdate;
import com.sofrecom.tickit.service.CaseService;
import com.sofrecom.tickit.util.AuthUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing Cases.
 */
@RestController
@RequestMapping("/api/v1/cases")
@Tag(name = "Case Management", description = "APIs for managing cases and their lifecycle")
public class CaseController {

    private final CaseService caseService;
    private final AuthUtils authUtils;

    public CaseController(CaseService caseService, AuthUtils authUtils) {
        this.caseService = caseService;
        this.authUtils = authUtils;
    }

    /**
     * Create a new Case.
     *
     * @param createCaseDTO the DTO with case creation details.
     * @return the created Case DTO.
     */
    @Operation(summary = "Create a new case", 
               description = "Creates a new case in the system with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", 
                    description = "Case created successfully", 
                    content = @Content(schema = @Schema(implementation = CaseResponse.class))),
        @ApiResponse(responseCode = "400", 
                    description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<CaseResponse> createCase(
            @Parameter(description = "Case details for creation", required = true) 
            @RequestBody CaseRequest createCaseDTO) {

                String userFingerprint = authUtils.generateUserFingerprint();
                createCaseDTO.setCreatedBy(userFingerprint); // Set the createdBy field with the user's fingerprint
        CaseResponse createdCase = caseService.createCase(createCaseDTO);

        return ResponseEntity.status(201).body(createdCase);
    }

    /**
     * Retrieve all cases.
     *
     * @return list of CaseResponse objects.
     */
    @Operation(summary = "Get all cases", 
               description = "Retrieves all cases in the system")
    @ApiResponse(responseCode = "200", 
                description = "List of cases retrieved successfully", 
                content = @Content(schema = @Schema(implementation = CaseResponse.class)))
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
    @Operation(summary = "Get a case by ID", 
               description = "Retrieves a specific case by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Case found", 
                    content = @Content(schema = @Schema(implementation = CaseResponse.class))),
        @ApiResponse(responseCode = "404", 
                    description = "Case not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CaseResponse> getCaseById(
            @Parameter(description = "Unique identifier of the case", required = true) 
            @PathVariable UUID id) {
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
    @Operation(summary = "Update a case", 
               description = "Updates an existing case with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Case updated successfully", 
                    content = @Content(schema = @Schema(implementation = CaseResponse.class))),
        @ApiResponse(responseCode = "400", 
                    description = "Invalid input data"),
        @ApiResponse(responseCode = "404", 
                    description = "Case not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CaseResponse> updateCase(
            @Parameter(description = "Unique identifier of the case to update", required = true) 
            @PathVariable UUID id, 
            @Parameter(description = "Updated case details", required = true) 
            @RequestBody CaseUpdate updateCaseDTO) {
        CaseResponse updatedCase = caseService.updateCase(id, updateCaseDTO);
        return ResponseEntity.ok(updatedCase);
    }

    /**
     * Delete a Case.
     *
     * @param id the case identifier.
     * @return a 204 No Content response.
     */
    @Operation(summary = "Delete a case", 
               description = "Deletes a case by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", 
                    description = "Case deleted successfully"),
        @ApiResponse(responseCode = "404", 
                    description = "Case not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCase(
            @Parameter(description = "Unique identifier of the case to delete", required = true) 
            @PathVariable UUID id) {
        caseService.deleteCase(id);
        return ResponseEntity.noContent().build();
    }
}
