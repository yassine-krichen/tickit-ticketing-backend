package com.sofrecom.tickit.service;

import java.util.List;
import java.util.Optional;

import com.sofrecom.tickit.model.LedgerEntry;

public interface LedgerService {
    void saveLedgerEntry(LedgerEntry entry);
    
    Optional<String> getLastEntryHash();
    
    List<LedgerEntry> getLedgerForEntity(String entityId, String entityType);
    
    boolean verifyIntegrity(LedgerEntry entry);
}
