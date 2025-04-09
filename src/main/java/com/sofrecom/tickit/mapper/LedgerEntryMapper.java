package com.sofrecom.tickit.mapper;

import com.sofrecom.tickit.model.LedgerEntry;
import com.sofrecom.tickit.dto.LedgerEntryRequest;
import com.sofrecom.tickit.dto.LedgerEntryResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class LedgerEntryMapper {

    public LedgerEntry toEntity(LedgerEntryRequest request) {
        return LedgerEntry.builder()
                .id(UUID.randomUUID())  // Generate new unique ID for every new ledger entry
                .entityId(request.getEntityId())
                .entityType(request.getEntityType())
                .caseId(request.getCaseId())
                .userFingerprint(request.getUserFingerprint())
                .actionType(request.getActionType())
                .dataSnapshot(request.getDataSnapshot())
                // Optionally set other fields, if needed.
                .build();
    }

    public LedgerEntryResponse toResponse(LedgerEntry entry) {
        return LedgerEntryResponse.builder()
                .id(entry.getId())
                .hash(entry.getHash())
                .previousHash(entry.getPreviousHash())
                .timestamp(entry.getTimestamp())
                .userFingerprint(entry.getUserFingerprint())
                .actionType(entry.getActionType())
                .entityType(entry.getEntityType())
                .entityId(entry.getEntityId())
                .caseId(entry.getCaseId())
                .dataSnapshot(entry.getDataSnapshot())
                .digitalSignature(entry.getDigitalSignature())
                .build();
    }
}
