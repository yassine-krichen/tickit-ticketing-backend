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

    @Query(value = "SELECT * FROM ledger_entry " +
               "WHERE data_snapshot @> CAST(:jsonValue AS jsonb)", nativeQuery = true)
    List<LedgerEntry> findByJsonFieldContains(@Param("jsonValue") String jsonValue);
}
