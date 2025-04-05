package com.sofrecom.tickit.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sofrecom.tickit.model.LedgerEntry;

public interface LedgerRepository extends JpaRepository<LedgerEntry, UUID> {
    Optional<LedgerEntry> findTopByOrderByTimestampDesc();

    List<LedgerEntry> findByEntityIdAndEntityTypeOrderByTimestampAsc(String entityId, String entityType);

    List<LedgerEntry> findByCaseId(String caseId);

    List<LedgerEntry> findByCaseIdAndEntityType(String caseId, String entityType);

    List<LedgerEntry> findAllByOrderByTimestampAsc();
}
