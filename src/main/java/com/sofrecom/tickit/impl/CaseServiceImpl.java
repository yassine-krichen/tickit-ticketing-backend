package com.sofrecom.tickit.impl;

import com.sofrecom.tickit.dto.CaseRequest;
import com.sofrecom.tickit.dto.CaseResponse;
import com.sofrecom.tickit.dto.CaseUpdate;
import com.sofrecom.tickit.exception.EntityCreationException;
import com.sofrecom.tickit.exception.ResourceNotFoundException;
import com.sofrecom.tickit.model.Case;
import com.sofrecom.tickit.repository.CaseRepository;
import com.sofrecom.tickit.service.CaseService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of the CaseService interface.
 */
@Service
public class CaseServiceImpl implements CaseService {

    private final CaseRepository caseRepository;

    public CaseServiceImpl(CaseRepository caseRepository) {
        this.caseRepository = caseRepository;
    }

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
