package com.sofrecom.tickit.dto;

import com.sofrecom.tickit.model.Case;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.sofrecom.tickit.model.CaseType;

/**
 * Data Transfer Object for returning Case details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseResponse {

    private UUID id;
    private String name;
    private String description;
    private List<String> tags;
    private CaseType type;
    private Case.Status status;
    private Case.Priority priority;
    private Case.Team team;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
