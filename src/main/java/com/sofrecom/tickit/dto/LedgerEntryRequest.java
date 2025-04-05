package com.sofrecom.tickit.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.sofrecom.tickit.model.LedgerEntry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LedgerEntryRequest {
    private String hash;
    private String previousHash;
    private LocalDateTime timestamp;
    private String userFingerprint;
    private LedgerEntry.ActionType actionType;
    private String entityType;
    private String entityId;
    private JsonNode dataSnapshot;
    private String digitalSignature;
}
