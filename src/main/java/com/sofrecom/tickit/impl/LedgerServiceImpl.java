package com.sofrecom.tickit.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

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
    
    public void saveLedgerEntry(LedgerEntry entry) {
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
    }
    
    public Optional<String> getLastEntryHash() {
        return ledgerRepository.findTopByOrderByTimestampDesc()
            .map(LedgerEntry::getHash);
    }
    
    public List<LedgerEntry> getLedgerForEntity(String entityId, String entityType) {
        return ledgerRepository.findByEntityIdAndEntityTypeOrderByTimestampAsc(entityId, entityType);
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
}
