package com.sofrecom.tickit.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.sofrecom.tickit.model.LedgerEntry;

public interface LedgerService {
    void saveLedgerEntry(LedgerEntry entry);
    
    Optional<String> getLastEntryHash();
        
    boolean verifyIntegrity(LedgerEntry entry);

    boolean verifyBlockchain();

    List<LedgerEntry> getLedgerForCase(String caseId, String entityType);

    public boolean verifyEntry(UUID entryId);

    // Query for JSON field
    List<LedgerEntry> getLedgerEntriesByJsonField(String jsonKey, String jsonValue);

    // Query for nested JSON field
    List<LedgerEntry> getLedgerEntriesByNestedJsonField(String jsonPath, String jsonValue);

    // Query for JSON field containing certain value
    List<LedgerEntry> getLedgerEntriesByJsonFieldContains(String jsonValue);

    List<LedgerEntry> findSimilarTreatmentActs(String type, String priority, int limit);
}
