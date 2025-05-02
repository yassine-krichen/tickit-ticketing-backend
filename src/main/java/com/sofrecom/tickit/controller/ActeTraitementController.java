package com.sofrecom.tickit.controller;

import com.sofrecom.tickit.dto.ActeTraitementRequest;
import com.sofrecom.tickit.dto.ActeTraitementResponse;
import com.sofrecom.tickit.dto.ActeTraitementUpdateRequest;
import com.sofrecom.tickit.dto.PopulatedTreatmentActResponse;
import com.sofrecom.tickit.service.ActeTraitementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

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
@RequestMapping("/api/v1/acte-traitements") // Added version
@RequiredArgsConstructor
@Tag(name = "Acte Traitement", description = "APIs for treatment act operations in the ticketing system")
public class ActeTraitementController {

    private final ActeTraitementService acteTraitementService;

    /**
     * Create a new treatment act.
     * POST /api/acte-traitements
     *
     * @param request Treatment act request
     * @return ResponseEntity containing the created treatment act
     */
    @Operation(summary = "Create a new treatment act", 
               description = "Creates a new treatment act based on the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", 
                    description = "Treatment act created successfully",
                    content = @Content(schema = @Schema(implementation = ActeTraitementResponse.class))),
        @ApiResponse(responseCode = "400", 
                    description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<ActeTraitementResponse> createActeTraitement(
            @Parameter(description = "Treatment act details to create", required = true)
            @RequestBody ActeTraitementRequest request) {
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
    @Operation(summary = "Get treatment act by ID", 
               description = "Returns a treatment act based on its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Treatment act found",
                    content = @Content(schema = @Schema(implementation = ActeTraitementResponse.class))),
        @ApiResponse(responseCode = "404", 
                    description = "Treatment act not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ActeTraitementResponse> getActeTraitement(
            @Parameter(description = "Unique identifier of the treatment act", required = true)
            @PathVariable String id) {
        ActeTraitementResponse acteTraitement = acteTraitementService.getActeTraitementById(id);
        return ResponseEntity.ok(acteTraitement);
    }

    /**
     * Retrieve all treatment acts.
     * GET /api/acte-traitements
     *
     * @return ResponseEntity containing the list of all treatment acts
     */
    @Operation(summary = "Get all treatment acts", 
               description = "Returns a list of all treatment acts in the system")
    @ApiResponse(responseCode = "200", 
                description = "List of treatment acts",
                content = @Content(schema = @Schema(implementation = ActeTraitementResponse.class)))
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
    @Operation(summary = "Get treatment acts by case ID", 
               description = "Returns all treatment acts associated with a specific case")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "List of treatment acts for the case",
                    content = @Content(schema = @Schema(implementation = ActeTraitementResponse.class))),
        @ApiResponse(responseCode = "400", 
                    description = "Invalid UUID format")
    })
    @GetMapping("/case/{caseId}")
    public ResponseEntity<List<ActeTraitementResponse>> getActeTraitementsByCaseId(
            @Parameter(description = "Unique identifier of the case", required = true)
            @PathVariable String caseId) {
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
    @Operation(summary = "Get number of steps in a case", 
               description = "Returns the count of treatment acts (steps) associated with a specific case")
    @ApiResponse(responseCode = "200", 
                description = "Number of steps",
                content = @Content(schema = @Schema(implementation = Map.class)))
    @GetMapping("/case/{caseId}/steps")
    public ResponseEntity<Map<String, Integer>> getNumberOfStepsByCaseId(
            @Parameter(description = "Unique identifier of the case", required = true)
            @PathVariable UUID caseId) {
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
    @Operation(summary = "Update a treatment act", 
               description = "Updates an existing treatment act with new data")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Treatment act updated successfully",
                    content = @Content(schema = @Schema(implementation = ActeTraitementResponse.class))),
        @ApiResponse(responseCode = "400", 
                    description = "Invalid update request"),
        @ApiResponse(responseCode = "404", 
                    description = "Treatment act not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ActeTraitementResponse> updateActeTraitement(
            @Parameter(description = "Unique identifier of the treatment act", required = true)
            @PathVariable String id, 
            @Parameter(description = "Updated treatment act details", required = true)
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
    @Operation(summary = "Delete a treatment act", 
               description = "Deletes a treatment act by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", 
                    description = "Treatment act deleted successfully"),
        @ApiResponse(responseCode = "404", 
                    description = "Treatment act not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActeTraitement(
            @Parameter(description = "Unique identifier of the treatment act to delete", required = true)
            @PathVariable String id) {
        acteTraitementService.deleteActeTraitement(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieve a treatment act by its ID with populated case and prestation data.
     * GET /api/acte-traitements/{id}/populated
     *
     * @param id Treatment act ID
     * @return ResponseEntity containing the found treatment act with populated data
     */
    @Operation(summary = "Get populated treatment act by ID", 
               description = "Returns a treatment act with detailed case and prestation information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Treatment act found with populated data",
                    content = @Content(schema = @Schema(implementation = PopulatedTreatmentActResponse.class))),
        @ApiResponse(responseCode = "404", 
                    description = "Treatment act not found")
    })
    @GetMapping("/{id}/populated")
    public ResponseEntity<PopulatedTreatmentActResponse> getPopulatedActeTraitement(
            @Parameter(description = "Unique identifier of the treatment act", required = true)
            @PathVariable String id) {
        PopulatedTreatmentActResponse acteTraitement = acteTraitementService.getPopulatedActeTraitementById(id);
        return ResponseEntity.ok(acteTraitement);
    }
}