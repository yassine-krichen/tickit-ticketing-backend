package com.sofrecom.tickit.controller;

import com.sofrecom.tickit.dto.BlockchainVerificationResponse;
import com.sofrecom.tickit.dto.LedgerEntryRequest;
import com.sofrecom.tickit.dto.LedgerEntryResponse;
import com.sofrecom.tickit.dto.VerificationResponse;
import com.sofrecom.tickit.mapper.LedgerEntryMapper;
import com.sofrecom.tickit.model.LedgerEntry;
import com.sofrecom.tickit.service.LedgerService;
import com.sofrecom.tickit.util.AuthUtils;

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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/ledger-entries")  // Changed from 'ledgers' to 'ledger-entries' to be more specific
@RequiredArgsConstructor
@Tag(name = "Ledger Management", description = "APIs for blockchain ledger operations")
public class LedgerController {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(LedgerController.class);

    private final LedgerService ledgerService;
    private final LedgerEntryMapper ledgerEntryMapper;
    private final AuthUtils authUtils;

    /**
     * Creates a new ledger entry using authenticated user information.
     *
     * @param request the ledger entry request DTO
     * @return a response containing the created ledger entry details
     */
    @Operation(summary = "Create a new ledger entry", 
               description = "Creates a new immutable entry in the blockchain ledger using authenticated user information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ledger entry created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    @PostMapping
    public ResponseEntity<LedgerEntryResponse> createLedgerEntry(
            @Parameter(description = "Ledger entry details for creation", required = true)
            @Valid @RequestBody LedgerEntryRequest request) {
            
            // Extract authenticated user info and override the userFingerprint
            String userFingerprint = authUtils.generateUserFingerprint();
            request.setUserFingerprint(userFingerprint);
            
            // Convert DTO to entity and save
            LedgerEntry entry = ledgerEntryMapper.toEntity(request);
            ledgerService.saveLedgerEntry(entry);
            
            // Prepare response
            LedgerEntryResponse response = ledgerEntryMapper.toResponse(entry);
            response.setStatus("SUCCESS");
            response.setMessage("Ledger entry created successfully");
            return ResponseEntity.ok(response);
    }

    /**
     * Verifies the integrity of a ledger entry.
     *
     * @param entryId the ledger entry identifier.
     * @return a verification response DTO indicating if the entry is valid.
     */
    @Operation(summary = "Verify a ledger entry", 
               description = "Verifies the integrity and authenticity of a specific ledger entry")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Verification completed", 
                    content = @Content(schema = @Schema(implementation = VerificationResponse.class))),
        @ApiResponse(responseCode = "404", 
                    description = "Ledger entry not found")
    })
    @GetMapping("/{entryId}/verification")
    public ResponseEntity<VerificationResponse> verifyEntry(
            @Parameter(description = "Unique identifier of the ledger entry to verify", required = true)
            @PathVariable UUID entryId) {
        boolean isValid = ledgerService.verifyEntry(entryId);
        VerificationResponse response = new VerificationResponse(isValid);
        return ResponseEntity.ok(response);
    }

    /**
     * Verify the integrity of the entire blockchain.
     * This method iterates over all ledger entries in order and checks the chain links.
     */
    @Operation(summary = "Verify the entire blockchain", 
               description = "Verifies the integrity of the entire blockchain by checking all entry links")
    @ApiResponse(responseCode = "200", 
                description = "Blockchain verification completed", 
                content = @Content(schema = @Schema(implementation = BlockchainVerificationResponse.class)))
    @GetMapping("/verification/blockchain")
    public ResponseEntity<BlockchainVerificationResponse> verifyBlockchain() {
        // Example implementation: The service will perform integrity checks over the entire chain.
        boolean isChainValid = ledgerService.verifyBlockchain();
        String message = isChainValid ? "Blockchain is valid" : "Blockchain integrity compromised";
        BlockchainVerificationResponse response = new BlockchainVerificationResponse(isChainValid, message);
        return ResponseEntity.ok(response);
    }

    /**
     * Get ledger entries for a specific case.
     * Optional query parameter "entityType" can be provided (CASE or TREATMENT_ACT).
     * If not provided, returns all entries for the case.
     */
    @Operation(summary = "Get ledger entries for a case", 
               description = "Retrieves all ledger entries associated with a specific case")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Ledger entries retrieved successfully", 
                    content = @Content(schema = @Schema(implementation = LedgerEntryResponse.class))),
        @ApiResponse(responseCode = "404", 
                    description = "Case not found")
    })
    @GetMapping
    public ResponseEntity<List<LedgerEntryResponse>> getLedgerEntries(
            @Parameter(description = "Unique identifier of the case")
            @RequestParam(required = false) String caseId,
            @Parameter(description = "Type of entity to filter by (CASE or TREATMENT_ACT)")
            @RequestParam(name = "entityType", required = false) String entityType) {
        List<LedgerEntry> entries = ledgerService.getLedgerForCase(caseId, entityType);
        List<LedgerEntryResponse> responses = entries.stream()
                .map(ledgerEntryMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * Get all treatment act ledger entries for a given case ordered by timestamp.
     */
    @Operation(summary = "Get ordered treatment act entries", 
               description = "Retrieves all treatment act ledger entries for a case ordered by timestamp")
    @ApiResponse(responseCode = "200", 
                description = "Ordered ledger entries retrieved successfully", 
                content = @Content(schema = @Schema(implementation = LedgerEntryResponse.class)))
    @GetMapping("/treatment-acts")
    public ResponseEntity<List<LedgerEntryResponse>> getTreatmentActsOrdered(
            @Parameter(description = "Unique identifier of the case", required = true)
            @RequestParam String caseId) {
        List<LedgerEntry> entries = ledgerService.getLedgerForCase(caseId, "TREATMENT_ACT");
        List<LedgerEntryResponse> responses = entries.stream()
                .sorted((a, b) -> a.getTimestamp().compareTo(b.getTimestamp()))
                .map(ledgerEntryMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // API to get ledger entries by JSON field
    @Operation(summary = "Get ledger entries by top-level JSON field", 
               description = "Searches for ledger entries where a specific top-level JSON field matches the exact value. " +
                            "This is for direct top-level properties only, not nested objects.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Matching ledger entries retrieved", 
                    content = @Content(schema = @Schema(implementation = LedgerEntry.class))),
        @ApiResponse(responseCode = "500", 
                    description = "Server error processing the request")
    })
    @GetMapping("/search/by-field")
    public ResponseEntity<List<LedgerEntry>> searchByJsonField(
            @Parameter(description = "JSON field name to search for (top-level only)", 
                      required = true, example = "status")
            @RequestParam String jsonKey, 
            @Parameter(description = "Exact value to match in the JSON field", 
                      required = true, example = "RESOLVED")
            @RequestParam String jsonValue) {
        try {
            List<LedgerEntry> entries = ledgerService.getLedgerEntriesByJsonField(jsonKey, jsonValue);
            return ResponseEntity.ok(entries);
        } catch (Exception e) {
            throw new RuntimeException("Error searching JSON field: " + e.getMessage(), e);
        }
    }

    // API to get ledger entries by nested JSON field
    @Operation(summary = "Get ledger entries by JSON path", 
               description = "Searches for ledger entries where a specific JSON path matches the exact value. " +
                            "This uses PostgreSQL's #>> operator for path extraction. " +
                            "The JSON path should be dot-separated without brackets (e.g., 'user.profile.name').")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Matching ledger entries retrieved", 
                    content = @Content(schema = @Schema(implementation = LedgerEntry.class))),
        @ApiResponse(responseCode = "500", 
                    description = "Server error processing the request, often due to invalid JSON path")
    })
    @GetMapping("/search/by-path")
    public ResponseEntity<List<LedgerEntry>> searchByJsonPath(
            @Parameter(description = "JSON path to search (e.g., 'status' or 'user.address.city')", 
                      required = true, example = "status")
            @RequestParam String jsonPath, 
            @Parameter(description = "Exact value to match at the specified JSON path", 
                      required = true, example = "RESOLVED")
            @RequestParam String jsonValue) {
        try {
            List<LedgerEntry> entries = ledgerService.getLedgerEntriesByNestedJsonField(jsonPath, jsonValue);
            return ResponseEntity.ok(entries);
        } catch (Exception e) {
            throw new RuntimeException("Error searching nested JSON path: " + e.getMessage(), e);
        }
    }

    // API to get ledger entries where JSON field contains a specific value
    @Operation(summary = "Search for text in ledger entries", 
               description = "Searches for ledger entries where the data snapshot contains the specified text. " +
                            "This performs a simple text search regardless of JSON structure. " +
                            "For example, search for 'value' to find entries with that text anywhere in the JSON.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                    description = "Matching ledger entries retrieved", 
                    content = @Content(schema = @Schema(implementation = LedgerEntry.class))),
        @ApiResponse(responseCode = "500", 
                    description = "Server error processing the request")
    })
    @GetMapping("/search/by-text")
    public ResponseEntity<?> searchByText(
            @Parameter(description = "Text to search for in the JSON data", 
                     required = true, example = "value")
            @RequestParam("jsonValue") String searchText) {
        try {
            if (searchText == null || searchText.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Search text cannot be empty");
            }
            
            // Sanitize the input to remove any potentially problematic characters
            String sanitizedText = searchText.replaceAll("[\\{\\}\\[\\]\\\"\\:\\%\\_]", "");
            
            // Check if sanitized text removed everything - if so, reject the request
            if (sanitizedText.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Search text contained only special characters");
            }
            
            List<LedgerEntry> entries = ledgerService.getLedgerEntriesByJsonFieldContains(sanitizedText);
            return ResponseEntity.ok(entries);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body("Error searching ledger entries: " + e.getMessage());
        }
    }

    // Add this endpoint to LedgerController class
    @Operation(summary = "Test endpoint for external system integration", 
          description = "Logs incoming payloads, generates user fingerprint, persists the ledger entry, and returns a response.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payload processed and ledger entry created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid payload format or processing error"),
        @ApiResponse(responseCode = "401", description = "User not authenticated (if security is strictly enforced and fingerprint generation fails)")
    })
    @PostMapping("/test-integration")
    public ResponseEntity<LedgerEntryResponse> testIntegration(
            @RequestHeader(value = "Authorization", required = false) String authHeader, // Logged but fingerprint comes from context
            @Valid @RequestBody LedgerEntryRequest request) {
        
        LOGGER.info("=== Test Integration Endpoint Called ===");
        LOGGER.info("Authorization Header (for logging purposes): {}", authHeader);
        LOGGER.info("Incoming Request Payload: {}", request);
        
        LedgerEntryResponse response;
        try {
            // 1. Generate user fingerprint from authenticated user context
            String userFingerprint = authUtils.generateUserFingerprint();
            request.setUserFingerprint(userFingerprint); // Override any incoming fingerprint
            LOGGER.info("Generated User Fingerprint: {}", userFingerprint);

            // 2. Convert DTO to entity
            LedgerEntry entryToSave = ledgerEntryMapper.toEntity(request);
            LOGGER.info("Mapped LedgerEntry entity for saving: {}", entryToSave);

            // 3. Persist the entry using the LedgerService
            // This service call will handle setting the timestamp, hashes, and digital signature.
            ledgerService.saveLedgerEntry(entryToSave);
            LOGGER.info("LedgerEntry from integration endpoint saved successfully with ID: {}", entryToSave.getId());

            // 4. Prepare response from the *saved* entity
            response = ledgerEntryMapper.toResponse(entryToSave);
            // Assuming LedgerEntryResponse is mutable or has a builder to add status/message
            // If LedgerEntryResponse is immutable and doesn't have these fields, 
            // you might need a wrapper DTO or adjust LedgerEntryResponse.
            // For now, I'll assume it's mutable like in the original createLedgerEntry.
            response.setStatus("INTEGRATION_SUCCESS"); 
            response.setMessage("Ledger entry from integration endpoint created, logged, and persisted successfully.");
            LOGGER.info("Returning successful response: {}", response);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            LOGGER.error("Error processing /test-integration request: {}", e.getMessage(), e);
            // Build an error response
            response = LedgerEntryResponse.builder()
                .id(null) // No ID if saving failed
                .entityType(request.getEntityType())
                .entityId(request.getEntityId())
                .timestamp(LocalDateTime.now())
                .status("INTEGRATION_ERROR")
                .message("Failed to process integration payload: " + e.getMessage())
                .build();
            // Consider returning a more specific HTTP status code based on the exception type
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
