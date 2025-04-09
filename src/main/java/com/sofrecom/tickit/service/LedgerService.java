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
}
