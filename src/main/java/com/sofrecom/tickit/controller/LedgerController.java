package com.sofrecom.tickit.controller;

import com.sofrecom.tickit.dto.BlockchainVerificationResponse;
import com.sofrecom.tickit.dto.LedgerEntryRequest;
import com.sofrecom.tickit.dto.LedgerEntryResponse;
import com.sofrecom.tickit.dto.VerificationResponse;
import com.sofrecom.tickit.mapper.LedgerEntryMapper;
import com.sofrecom.tickit.model.LedgerEntry;
import com.sofrecom.tickit.service.LedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;

@RestController
@RequestMapping("/ledger")
@RequiredArgsConstructor
public class LedgerController {

    private final LedgerService ledgerService;
    private final LedgerEntryMapper ledgerEntryMapper;

    /**
     * Creates a new ledger entry.
     *
     * @param request the ledger entry request DTO.
     * @return a friendly response model with status and message.
     */
    @PostMapping("/entry")
    public ResponseEntity<LedgerEntryResponse> createLedgerEntry(@Valid @RequestBody LedgerEntryRequest request) {
            // Convert DTO to entity. Note: Ignore system-managed fields from request.
            LedgerEntry entry = ledgerEntryMapper.toEntity(request);
            ledgerService.saveLedgerEntry(entry);
            
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
    @GetMapping("/verify/{entryId}")
    public ResponseEntity<VerificationResponse> verifyEntry(@PathVariable UUID entryId) {
        boolean isValid = ledgerService.verifyEntry(entryId);
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
                .map(ledgerEntryMapper::toResponse)
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
                .map(ledgerEntryMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // API to get ledger entries by JSON field
    @GetMapping("/data-snapshot")
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
    
}
