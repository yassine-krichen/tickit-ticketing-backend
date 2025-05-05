package com.sofrecom.tickit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sofrecom.tickit.config.TestSecurityConfig;
import com.sofrecom.tickit.dto.CaseRequest;
import com.sofrecom.tickit.dto.CaseResponse;
import com.sofrecom.tickit.dto.CaseUpdate;
import com.sofrecom.tickit.model.Case;
import com.sofrecom.tickit.model.CaseType;
import com.sofrecom.tickit.service.CaseService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for the CaseController endpoints.
 */
@WebMvcTest(CaseController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
public class CaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CaseService caseService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "test_user", roles = "ADMIN")
    @DisplayName("POST /api/v1/cases - Success")
    public void testCreateCase() throws Exception {
        CaseRequest request = CaseRequest.builder()
                .name("Test Case")
                .description("Test Description")
                .tags(Collections.singletonList("tag1"))
                .type(CaseType.GRAFIC)
                .status(Case.Status.ONGOING)
                .priority(Case.Priority.MEDIUM)
                .team(Case.Team.TUNISIA)
                .createdBy("tester")
                .build();

        CaseResponse response = CaseResponse.builder()
                .id(UUID.randomUUID())
                .name(request.getName())
                .description(request.getDescription())
                .tags(request.getTags())
                .type(request.getType())
                .status(request.getStatus())
                .priority(request.getPriority())
                .team(request.getTeam())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Mockito.when(caseService.createCase(Mockito.any(CaseRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/cases")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(request.getName())))
                .andExpect(jsonPath("$.description", is(request.getDescription())));
    }

    @Test
    @WithMockUser(username = "test_user", roles = "ADMIN")
    @DisplayName("GET /api/v1/cases - Success")
    public void testGetAllCases() throws Exception {
        CaseResponse response = CaseResponse.builder()
                .id(UUID.randomUUID())
                .name("Test Case")
                .description("Test Description")
                .tags(Collections.singletonList("tag1"))
                .type(CaseType.XOIP)
                .status(Case.Status.ONGOING)
                .priority(Case.Priority.HIGH)
                .team(Case.Team.MOROCCO)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Mockito.when(caseService.getAllCases()).thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/api/v1/cases")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(response.getName())));
    }

    @Test
    @WithMockUser(username = "test_user", roles = "ADMIN")
    @DisplayName("GET /api/v1/cases/{id} - Success")
    public void testGetCaseById() throws Exception {
        UUID id = UUID.randomUUID();
        CaseResponse response = CaseResponse.builder()
                .id(id)
                .name("Test Case")
                .description("Test Description")
                .tags(Collections.singletonList("tag1"))
                .type(CaseType.TRAVAUX_TIERS)
                .status(Case.Status.ONGOING)
                .priority(Case.Priority.LOW)
                .team(Case.Team.TUNISIA)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Mockito.when(caseService.getCaseById(id)).thenReturn(response);

        mockMvc.perform(get("/api/v1/cases/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.toString())))
                .andExpect(jsonPath("$.name", is(response.getName())));
    }

    @Test
    @WithMockUser(username = "test_user", roles = "ADMIN")
    @DisplayName("PUT /api/v1/cases/{id} - Success")
    public void testUpdateCase() throws Exception {
        UUID id = UUID.randomUUID();

        CaseUpdate updateRequest = CaseUpdate.builder()
                .name("Updated Case")
                .description("Updated Description")
                .tags(Collections.singletonList("tag1"))
                .type(CaseType.MUTATION)
                .status(Case.Status.ONGOING)
                .priority(Case.Priority.HIGH)
                .team(Case.Team.MOROCCO)
                .build();

        CaseResponse response = CaseResponse.builder()
                .id(id)
                .name(updateRequest.getName())
                .description(updateRequest.getDescription())
                .tags(updateRequest.getTags())
                .type(updateRequest.getType())
                .status(updateRequest.getStatus())
                .priority(updateRequest.getPriority())
                .team(updateRequest.getTeam())
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now())
                .build();

        Mockito.when(caseService.updateCase(Mockito.eq(id), Mockito.any(CaseUpdate.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/cases/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(updateRequest.getName())));
    }

    @Test
    @WithMockUser(username = "test_user", roles = "ADMIN")
    @DisplayName("DELETE /api/v1/cases/{id} - Success")
    public void testDeleteCase() throws Exception {
        UUID id = UUID.randomUUID();

        // When a deletion is successful, the service layer returns void.
        Mockito.doNothing().when(caseService).deleteCase(id);

        mockMvc.perform(delete("/api/v1/cases/{id}", id))
                .andExpect(status().isNoContent());
    }
}
