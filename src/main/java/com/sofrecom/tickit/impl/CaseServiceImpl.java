package com.sofrecom.tickit.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sofrecom.tickit.dto.CaseRequest;
import com.sofrecom.tickit.dto.CaseResponse;
import com.sofrecom.tickit.dto.CaseUpdate;
import com.sofrecom.tickit.exception.EntityCreationException;
import com.sofrecom.tickit.exception.ResourceNotFoundException;
import com.sofrecom.tickit.model.Case;
import com.sofrecom.tickit.model.LedgerEntry;
import com.sofrecom.tickit.repository.CaseRepository;
import com.sofrecom.tickit.service.CaseService;
import com.sofrecom.tickit.service.LedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of the CaseService interface.
 * Manages the lifecycle of cases including creation, retrieval, updates, and deletion.
 */
@Service
@RequiredArgsConstructor
public class CaseServiceImpl implements CaseService {

    private final CaseRepository caseRepository;
    private final LedgerService ledgerService;
    private final ObjectMapper objectMapper;

    @Override
    public CaseResponse createCase(CaseRequest createCaseRequest) {
        // Map CreateCaseRequest to Case entity
        Case newCase = new Case();
        newCase.setName(createCaseRequest.getName());
        newCase.setDescription(createCaseRequest.getDescription());
        newCase.setTags(createCaseRequest.getTags());
        newCase.setType(createCaseRequest.getType());
        newCase.setStatus(createCaseRequest.getStatus());
        newCase.setPriority(createCaseRequest.getPriority());
        newCase.setTeam(createCaseRequest.getTeam());
        newCase.setCreatedBy(createCaseRequest.getCreatedBy());
        newCase.setCreatedAt(LocalDateTime.now());
        newCase.setUpdatedAt(LocalDateTime.now());

        try {
            Case savedCase = caseRepository.save(newCase);
            createCaseLedgerEntry(savedCase, LedgerEntry.ActionType.CREATE, createCaseRequest.getCreatedBy());
            return mapToResponse(savedCase);
        } catch (Exception ex) {
            throw new EntityCreationException("Case", "Unable to create case", ex);
        }
    }

    @Override
    public List<CaseResponse> getAllCases() {
        return caseRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CaseResponse getCaseById(UUID id) {
        Case foundCase = caseRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("Case", "id", id)
        );
        return mapToResponse(foundCase);
    }

    @Override
    public CaseResponse updateCase(UUID id, CaseUpdate updateCaseDTO) {
        Case existingCase = caseRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("Case", "id", id)
        );

        // Update fields
        existingCase.setName(updateCaseDTO.getName());
        existingCase.setDescription(updateCaseDTO.getDescription());
        existingCase.setTags(updateCaseDTO.getTags());
        existingCase.setType(updateCaseDTO.getType());
        existingCase.setStatus(updateCaseDTO.getStatus());
        existingCase.setPriority(updateCaseDTO.getPriority());
        existingCase.setTeam(updateCaseDTO.getTeam());
        existingCase.setUpdatedAt(LocalDateTime.now());

        Case updatedCase = caseRepository.save(existingCase);
        return mapToResponse(updatedCase);
    }

    @Override
    public void deleteCase(UUID id) {
        Case existingCase = caseRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("Case", "id", id)
        );
        caseRepository.delete(existingCase);
    }

    /**
     * Create a blockchain ledger entry for a case operation.
     *
     * @param caseEntity Case entity
     * @param actionType Type of action (CREATE, UPDATE, DELETE)
     * @param userFingerprint User identifier
     */
    private void createCaseLedgerEntry(Case caseEntity, LedgerEntry.ActionType actionType, String userFingerprint) {
        // Create JSON snapshot of current case state
        JsonNode dataSnapshot = createCaseJsonSnapshot(caseEntity);
        
        // Create ledger entry
        LedgerEntry ledgerEntry = LedgerEntry.builder()
                .id(UUID.randomUUID())
                .timestamp(LocalDateTime.now())
                .userFingerprint(userFingerprint)
                .actionType(actionType)
                .entityType("CASE")
                .entityId(caseEntity.getId().toString())
                .caseId(caseEntity.getId().toString())
                .dataSnapshot(dataSnapshot)
                .build();
        
        // Use the ledger service to save the entry with proper hash calculation
        ledgerService.saveLedgerEntry(ledgerEntry);
    }

    /**
     * Create a JSON representation of a case.
     *
     * @param caseEntity Case entity
     * @return JsonNode representing the entity state
     */
    private JsonNode createCaseJsonSnapshot(Case caseEntity) {
        ObjectNode node = objectMapper.createObjectNode();
        
        node.put("id", caseEntity.getId().toString());
        node.put("name", caseEntity.getName());
        node.put("description", caseEntity.getDescription());
        node.put("type", caseEntity.getType().toString());
        node.put("status", caseEntity.getStatus().toString());
        node.put("priority", caseEntity.getPriority().toString());
        node.put("team", caseEntity.getTeam().toString());
        node.put("createdAt", caseEntity.getCreatedAt().toString());
        node.put("createdBy", caseEntity.getCreatedBy());
        node.put("updatedAt", caseEntity.getUpdatedAt().toString());
        
        return node;
    }

    /**
     * Converts a Case entity to its corresponding ResponseDTO Class.
     * @param cas the Case entity.
     * @return the CaseResponse.
     */
    private CaseResponse mapToResponse(Case cas) {
        return new CaseResponse(
            cas.getId(),
            cas.getName(),
            cas.getDescription(),
            cas.getTags(),
            cas.getType(),
            cas.getStatus(),
            cas.getPriority(),
            cas.getTeam(),
            cas.getCreatedAt(),
            cas.getUpdatedAt()
        );
    }
}
