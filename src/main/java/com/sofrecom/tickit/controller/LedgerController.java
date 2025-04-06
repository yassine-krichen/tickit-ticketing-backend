package com.sofrecom.tickit.controller;

import com.sofrecom.tickit.dto.BlockchainVerificationResponse;
import com.sofrecom.tickit.dto.LedgerEntryRequest;
import com.sofrecom.tickit.dto.LedgerEntryResponse;
import com.sofrecom.tickit.dto.VerificationResponse;
import com.sofrecom.tickit.exception.LedgerCreationException;
import com.sofrecom.tickit.model.LedgerEntry;
import com.sofrecom.tickit.repository.LedgerRepository;
import com.sofrecom.tickit.service.LedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;

@RestController
@RequestMapping("/ledger")
@RequiredArgsConstructor
public class LedgerController {

    private final LedgerService ledgerService;
    private final LedgerRepository ledgerRepository;

    /**
     * Creates a new ledger entry.
     *
     * @param request the ledger entry request DTO.
     * @return a friendly response model with status and message.
     */
    @PostMapping("/entry")
    public ResponseEntity<LedgerEntryResponse> createLedgerEntry(@Valid @RequestBody LedgerEntryRequest request) {
        try {
            // Convert DTO to entity. Note: Ignore system-managed fields from request.
            LedgerEntry entry = convertRequestToEntity(request);
            ledgerService.saveLedgerEntry(entry);
            
            LedgerEntryResponse response = convertEntityToResponse(entry);
            response.setStatus("SUCCESS");
            response.setMessage("Ledger entry created successfully");
            return ResponseEntity.ok(response);
        } catch (LedgerCreationException e) {
            LedgerEntryResponse errorResponse = LedgerEntryResponse.builder()
                    .status("FAILURE")
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Verifies the integrity of a ledger entry.
     *
     * @param entryId the ledger entry identifier.
     * @return a verification response DTO indicating if the entry is valid.
     */
    @GetMapping("/verify/{entryId}")
    public ResponseEntity<VerificationResponse> verifyEntry(@PathVariable UUID entryId) {
        Optional<LedgerEntry> entryOpt = ledgerRepository.findById(entryId);
        if (!entryOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        LedgerEntry entry = entryOpt.get();
        boolean isValid = ledgerService.verifyIntegrity(entry);
        VerificationResponse response = new VerificationResponse(isValid);
        return ResponseEntity.ok(response);
    }

    /**
     * Verify the integrity of the entire blockchain.
     * This method iterates over all ledger entries in order and checks the chain links.
     */
    @GetMapping("/verify/blockchain")
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
    @GetMapping("/case/{caseId}")
    public ResponseEntity<List<LedgerEntryResponse>> getEntriesForCase(
            @PathVariable String caseId,
            @RequestParam(name = "entityType", required = false) String entityType) {
        List<LedgerEntry> entries = ledgerService.getLedgerForCase(caseId, entityType);
        List<LedgerEntryResponse> responses = entries.stream()
                .map(this::convertEntityToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * Get all treatment act ledger entries for a given case ordered by timestamp.
     */
    @GetMapping("/treatment-act/{caseId}/ordered")
    public ResponseEntity<List<LedgerEntryResponse>> getTreatmentActsOrdered(@PathVariable String caseId) {
        List<LedgerEntry> entries = ledgerService.getLedgerForCase(caseId, "TREATMENT_ACT");
        List<LedgerEntryResponse> responses = entries.stream()
                .sorted((a, b) -> a.getTimestamp().compareTo(b.getTimestamp()))
                .map(this::convertEntityToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // API to get ledger entries by JSON field
    @GetMapping("data-snapshot")
    public ResponseEntity<List<LedgerEntry>> getLedgerEntriesByJsonField(
            @RequestParam String jsonKey, 
            @RequestParam String jsonValue) {
        
        List<LedgerEntry> entries = ledgerService.getLedgerEntriesByJsonField(jsonKey, jsonValue);
        return ResponseEntity.ok(entries);
    }

    // API to get ledger entries by nested JSON field
    @GetMapping("data-snapshot/nested")
    public ResponseEntity<List<LedgerEntry>> getLedgerEntriesByNestedJsonField(
            @RequestParam String jsonPath, 
            @RequestParam String jsonValue) {
        
        List<LedgerEntry> entries = ledgerService.getLedgerEntriesByNestedJsonField(jsonPath, jsonValue);
        return ResponseEntity.ok(entries);
    }

    // API to get ledger entries where JSON field contains a specific value
    @GetMapping("data-snapshot/contains")
    public ResponseEntity<List<LedgerEntry>> getLedgerEntriesByJsonFieldContains(
            @RequestParam("jsonValue") String jsonValue) {
                System.out.println(jsonValue);
        
        List<LedgerEntry> entries = ledgerService.getLedgerEntriesByJsonFieldContains(jsonValue);
        return ResponseEntity.ok(entries);
    }

    // --- Helper Methods ---
    // TODO: Move to a separate utility class or the service layer
    
    private LedgerEntry convertRequestToEntity(LedgerEntryRequest request) {
        return LedgerEntry.builder()
                .id(UUID.randomUUID())
                .entityId(request.getEntityId())
                .entityType(request.getEntityType())
                .caseId(request.getCaseId())         // Set the caseId
                .userFingerprint(request.getUserFingerprint())
                .actionType(request.getActionType())
                .dataSnapshot(request.getDataSnapshot())
                .build();
    }
    
    private LedgerEntryResponse convertEntityToResponse(LedgerEntry entry) {
        return LedgerEntryResponse.builder()
                .id(entry.getId())
                .hash(entry.getHash())
                .previousHash(entry.getPreviousHash())
                .timestamp(entry.getTimestamp())
                .userFingerprint(entry.getUserFingerprint())
                .actionType(entry.getActionType())
                .entityType(entry.getEntityType())
                .entityId(entry.getEntityId())
                .caseId(entry.getCaseId())           // Map the caseId
                .dataSnapshot(entry.getDataSnapshot())
                .digitalSignature(entry.getDigitalSignature())
                .build();
    }
    
}
