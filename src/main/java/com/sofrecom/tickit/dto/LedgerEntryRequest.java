package com.sofrecom.tickit.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.sofrecom.tickit.model.LedgerEntry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LedgerEntryRequest {

    @NotBlank(message = "Entity ID is required")
    private String entityId;

    @NotBlank(message = "Entity type is required")
    @Pattern(regexp = "CASE|TREATMENT_ACT", message = "Entity type must be either CASE or TREATMENT_ACT")
    private String entityType;

    // for testing purposes, this is set to not required
    // @NotBlank(message = "Case ID is required")
    private String caseId;

    // @NotBlank(message = "User fingerprint is required")
    // Example pattern: letters separated by dots: last_name.first_name.role.team (e.g. "reed.noa.producer.tunisia")
    @Pattern(regexp = "^[a-zA-Z]+\\.[a-zA-Z]+\\.[a-zA-Z]+\\.[a-zA-Z]+$", 
             message = "Fingerprint must be in format last_name.first_name.role.team")
    private String userFingerprint;

    @NotNull(message = "Action type is required")
    private LedgerEntry.ActionType actionType;

    @NotNull(message = "Data snapshot is required")
    private JsonNode dataSnapshot;
}
