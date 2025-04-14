package com.sofrecom.tickit.impl;

import com.sofrecom.tickit.dto.CaseRequest;
import com.sofrecom.tickit.dto.CaseResponse;
import com.sofrecom.tickit.exception.ResourceNotFoundException;
import com.sofrecom.tickit.model.Case;
import com.sofrecom.tickit.repository.CaseRepository;
import com.sofrecom.tickit.model.CaseType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
public class CaseServiceImplTest {

    @Mock
    private CaseRepository caseRepository;

    @InjectMocks
    private CaseServiceImpl caseService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("createCase() - Success")
    public void testCreateCase() {
        CaseRequest request = CaseRequest.builder()
                .name("New Case")
                .description("New Description")
                .tags(Collections.singletonList("tag1"))
                .type(CaseType.GRAFIC)
                .status(Case.Status.ONGOING)
                .priority(Case.Priority.MEDIUM)
                .team(Case.Team.TUNISIA)
                .createdBy("tester")
                .build();

        Case newCase = new Case();
        newCase.setName(request.getName());
        newCase.setDescription(request.getDescription());
        newCase.setTags(request.getTags());
        newCase.setType(request.getType());
        newCase.setStatus(request.getStatus());
        newCase.setPriority(request.getPriority());
        newCase.setTeam(request.getTeam());
        newCase.setCreatedBy(request.getCreatedBy());
        newCase.setCreatedAt(LocalDateTime.now());
        newCase.setUpdatedAt(LocalDateTime.now());

        // Simulate repository saving by returning the entity with an ID.
        UUID id = UUID.randomUUID();
        newCase.setId(id);
        when(caseRepository.save(any(Case.class))).thenReturn(newCase);

        // Create the case.
        CaseResponse response = caseService.createCase(request);
        assertNotNull(response);
        assertEquals("New Case", response.getName());
        verify(caseRepository, times(1)).save(any(Case.class));
    }

    @Test
    @DisplayName("getCaseById() - Not Found Exception")
    public void testGetCaseByIdNotFound() {
        UUID id = UUID.randomUUID();
        when(caseRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> caseService.getCaseById(id));
    }

    // You can add additional tests for updateCase() and deleteCase() following similar structure.
}
