package com.sofrecom.tickit.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.sofrecom.tickit.exception.EntityCreationException;
import com.sofrecom.tickit.exception.ResourceNotFoundException;
import com.sofrecom.tickit.model.LedgerEntry;
import com.sofrecom.tickit.repository.LedgerRepository;
import com.sofrecom.tickit.service.LedgerService;
import com.sofrecom.tickit.util.CryptoUtil;


@Service
public class LedgerServiceImpl implements LedgerService {
    
    private final LedgerRepository ledgerRepository;
    private final CryptoUtil cryptoUtil;
    
    public LedgerServiceImpl(LedgerRepository ledgerRepository, CryptoUtil cryptoUtil) {
        this.ledgerRepository = ledgerRepository;
        this.cryptoUtil = cryptoUtil;
    }
    
    @Override
    public void saveLedgerEntry(LedgerEntry entry) {
        try {
            // Auto-create the timestamp at the moment of saving
            entry.setTimestamp(LocalDateTime.now());

            // Get previous hash from last ledger entry or "GENESIS" if none exist
            String previousHash = getLastEntryHash().orElse("GENESIS");
            String dataHash = cryptoUtil.computeHash(
                    entry.getDataSnapshot().toString(), 
                    previousHash, 
                    entry.getTimestamp().toString()
            );
            
            entry.setPreviousHash(previousHash);
            entry.setHash(dataHash);
            entry.setDigitalSignature(cryptoUtil.signData(dataHash));
            
            ledgerRepository.save(entry);
        } catch (Exception e) {
            throw new EntityCreationException("LedgerEntry", "Failed to create ledger entry", e);
        }
    }
    
    public Optional<String> getLastEntryHash() {
        return ledgerRepository.findTopByOrderByTimestampDesc()
            .map(LedgerEntry::getHash);
    }
    
    
    public boolean verifyIntegrity(LedgerEntry entry) {
        String computedHash = cryptoUtil.computeHash(
            entry.getDataSnapshot().toString(),
            entry.getPreviousHash(),
            entry.getTimestamp().toString()
        );
        
        boolean hashValid = computedHash.equals(entry.getHash());
        boolean signatureValid = cryptoUtil.verifySignature(
            entry.getHash(), 
            entry.getDigitalSignature()
        );
        
        return hashValid && signatureValid;
    }

    @Override
    public boolean verifyEntry(UUID entryId) {
        LedgerEntry entry = ledgerRepository.findById(entryId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "LedgerEntry", 
                "Id",
                entryId
            ));

        return verifyIntegrity(entry);
    }

    @Override
    public boolean verifyBlockchain() {
        List<LedgerEntry> allEntries = ledgerRepository.findAllByOrderByTimestampAsc();
        String previousHash = "GENESIS";
        for (LedgerEntry entry : allEntries) {
            String computedHash = cryptoUtil.computeHash(
                entry.getDataSnapshot().toString(),
                previousHash,
                entry.getTimestamp().toString()
            );
            if (!computedHash.equals(entry.getHash()) || !cryptoUtil.verifySignature(entry.getHash(), entry.getDigitalSignature())) {
                return false;
            }
            previousHash = entry.getHash();
        }
        return true;
    }

    @Override
    public List<LedgerEntry> getLedgerForCase(String caseId, String entityType) {
        if (entityType != null && !entityType.isEmpty()) {
            return ledgerRepository.findByCaseIdAndEntityType(caseId, entityType);
        } else {
            return ledgerRepository.findByCaseId(caseId);
        }
    }
}
