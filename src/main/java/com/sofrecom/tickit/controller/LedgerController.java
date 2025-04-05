package com.sofrecom.tickit.controller;

import com.sofrecom.tickit.dto.LedgerEntryRequest;
import com.sofrecom.tickit.dto.LedgerEntryResponse;
import com.sofrecom.tickit.dto.VerificationResponse;
import com.sofrecom.tickit.model.LedgerEntry;
import com.sofrecom.tickit.repository.LedgerRepository;
import com.sofrecom.tickit.service.LedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
     * @return the created ledger entry's id.
     */
    @PostMapping("/entry")
    public ResponseEntity<?> createLedgerEntry(@RequestBody LedgerEntryRequest request) {
        // Validate input
        if (request.getEntityId() == null || request.getEntityType() == null) {
            return ResponseEntity.badRequest().body("Entity identifiers are required");
        }

        // Convert DTO to entity and save it
        LedgerEntry entry = convertRequestToEntity(request);
        ledgerService.saveLedgerEntry(entry);

        return ResponseEntity.ok(entry.getId());
    }

    /**
     * Retrieves the ledger entries for a case.
     *
     * @param caseId the case identifier.
     * @return list of ledger entry response DTOs.
     */
    @GetMapping("/case/{caseId}")
    public ResponseEntity<List<LedgerEntryResponse>> getLedgerForCase(@PathVariable String caseId) {
        List<LedgerEntry> entries = ledgerService.getLedgerForEntity(caseId, "CASE");
        List<LedgerEntryResponse> responses = entries.stream()
                .map(this::convertEntityToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * Retrieves the ledger entries for a treatment act.
     *
     * @param actId the treatment act identifier.
     * @return list of ledger entry response DTOs.
     */
    @GetMapping("/treatment-act/{actId}")
    public ResponseEntity<List<LedgerEntryResponse>> getLedgerForTreatmentAct(@PathVariable String actId) {
        List<LedgerEntry> entries = ledgerService.getLedgerForEntity(actId, "TREATMENT_ACT");
        List<LedgerEntryResponse> responses = entries.stream()
                .map(this::convertEntityToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
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

    // --- Helper Methods ---

    private LedgerEntry convertRequestToEntity(LedgerEntryRequest request) {
        // Example conversion. In practice, you may need to enrich the entity with additional data.
        return LedgerEntry.builder()
                .id(UUID.randomUUID())
                .hash(request.getHash())  // This could be computed in the service layer
                .previousHash(request.getPreviousHash())
                .timestamp(request.getTimestamp())
                .userFingerprint(request.getUserFingerprint())
                .actionType(request.getActionType())
                .entityType(request.getEntityType())
                .entityId(request.getEntityId())
                .dataSnapshot(request.getDataSnapshot())
                .digitalSignature(request.getDigitalSignature())
                .build();
    }

    private LedgerEntryResponse convertEntityToResponse(LedgerEntry entry) {
        // Example conversion. Adjust the fields according to your needs.
        return LedgerEntryResponse.builder()
                .id(entry.getId())
                .hash(entry.getHash())
                .previousHash(entry.getPreviousHash())
                .timestamp(entry.getTimestamp())
                .userFingerprint(entry.getUserFingerprint())
                .actionType(entry.getActionType())
                .entityType(entry.getEntityType())
                .entityId(entry.getEntityId())
                .dataSnapshot(entry.getDataSnapshot())
                .digitalSignature(entry.getDigitalSignature())
                .build();
    }
}
