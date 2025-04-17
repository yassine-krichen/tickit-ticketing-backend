package com.sofrecom.tickit.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sofrecom.tickit.model.LedgerEntry;

public interface LedgerRepository extends JpaRepository<LedgerEntry, UUID> {
    Optional<LedgerEntry> findTopByOrderByTimestampDesc();

    List<LedgerEntry> findByEntityIdAndEntityTypeOrderByTimestampAsc(String entityId, String entityType);

    List<LedgerEntry> findByCaseId(String caseId);

    List<LedgerEntry> findByCaseIdAndEntityType(String caseId, String entityType);

    List<LedgerEntry> findAllByOrderByTimestampAsc();

    @Query(value = "SELECT * FROM ledger_entry " +
               "WHERE data_snapshot ->> :jsonKey = :jsonValue", nativeQuery = true)
    List<LedgerEntry> findByJsonField(@Param("jsonKey") String jsonKey, @Param("jsonValue") String jsonValue);

    @Query(value = "SELECT * FROM ledger_entry " +
               "WHERE data_snapshot #>> string_to_array(:jsonPath, '.') = :jsonValue", nativeQuery = true)
    List<LedgerEntry> findByNestedJsonField(@Param("jsonPath") String jsonPath, @Param("jsonValue") String jsonValue);

    /**
     * Find ledger entries where the data snapshot contains the specified text.
     * This converts the JSONB to text and uses PostgreSQL's position function to check for text
     * containment, which is more reliable than ILIKE for this purpose.
     * 
     * @param searchText A simple text string to search for in the data snapshot
     * @return List of matching ledger entries
     */
    @Query(value = "SELECT * FROM ledger_entry " +
               "WHERE position(lower(?1) in lower(cast(data_snapshot as text))) > 0", nativeQuery = true)
    List<LedgerEntry> findByJsonFieldContains(String searchText);
}
