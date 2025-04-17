package com.sofrecom.tickit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sofrecom.tickit.dto.ActeTraitementRequest;
import com.sofrecom.tickit.dto.ActeTraitementResponse;
import com.sofrecom.tickit.dto.ActeTraitementUpdateRequest;
import com.sofrecom.tickit.service.ActeTraitementService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for the ActeTraitementController endpoints.
 */
@WebMvcTest(ActeTraitementController.class)
@ActiveProfiles("test")
public class ActeTraitementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ActeTraitementService acteTraitementService;

    @Autowired
    private ObjectMapper objectMapper;

    private final String TEST_ID = "act-123";
    private final UUID TEST_CASE_ID = UUID.randomUUID();
    private final int TEST_PRESTATION_ID = 1;
    private final String TEST_TARIF_ID = "tarif-123";

    @Test
    @DisplayName("POST /api/acte-traitements - Success")
    public void testCreateActeTraitement() throws Exception {
        // Arrange
        ActeTraitementRequest request = createSampleRequest();
        ActeTraitementResponse response = createSampleResponse(TEST_ID, request);
        
        when(acteTraitementService.createActeTraitement(any(ActeTraitementRequest.class)))
            .thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/acte-traitements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idacte", is(TEST_ID)))
                .andExpect(jsonPath("$.type_element", is(request.getType_element())))
                .andExpect(jsonPath("$.quantite", is(request.getQuantite())));
    }

    @Test
    @DisplayName("GET /api/acte-traitements/{id} - Success")
    public void testGetActeTraitement() throws Exception {
        // Arrange
        ActeTraitementResponse response = createSampleResponse(TEST_ID, createSampleRequest());
        
        when(acteTraitementService.getActeTraitementById(TEST_ID))
            .thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/acte-traitements/{id}", TEST_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idacte", is(TEST_ID)))
                .andExpect(jsonPath("$.type_element", is(response.getType_element())));
    }

    @Test
    @DisplayName("GET /api/acte-traitements - Success")
    public void testGetAllActeTraitements() throws Exception {
        // Arrange
        List<ActeTraitementResponse> responses = Arrays.asList(
            createSampleResponse("act-1", createSampleRequest()),
            createSampleResponse("act-2", createSampleRequest())
        );
        
        when(acteTraitementService.getAllActeTraitements())
            .thenReturn(responses);

        // Act & Assert
        mockMvc.perform(get("/api/acte-traitements")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].idacte", is("act-1")))
                .andExpect(jsonPath("$[1].idacte", is("act-2")));
    }

    @Test
    @DisplayName("GET /api/acte-traitements/case/{caseId} - Success")
    public void testGetActeTraitementsByCaseId() throws Exception {
        // Arrange
        List<ActeTraitementResponse> responses = Collections.singletonList(
            createSampleResponse(TEST_ID, createSampleRequest())
        );
        
        when(acteTraitementService.getActeTraitementsByCaseId(TEST_CASE_ID))
            .thenReturn(responses);

        // Act & Assert
        mockMvc.perform(get("/api/acte-traitements/case/{caseId}", TEST_CASE_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].idacte", is(TEST_ID)));
    }

    @Test
    @DisplayName("GET /api/acte-traitements/case/{caseId}/steps - Success")
    public void testGetNumberOfStepsByCaseId() throws Exception {
        // Arrange
        int numberOfSteps = 5;
        
        when(acteTraitementService.getNumberOfStepsByCaseId(TEST_CASE_ID))
            .thenReturn(numberOfSteps);

        // Act & Assert
        mockMvc.perform(get("/api/acte-traitements/case/{caseId}/steps", TEST_CASE_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.steps", is(numberOfSteps)));
    }

    @Test
    @DisplayName("PUT /api/acte-traitements/{id} - Success")
    public void testUpdateActeTraitement() throws Exception {
        // Arrange
        ActeTraitementUpdateRequest updateRequest = createSampleUpdateRequest();
                
        ActeTraitementResponse response = ActeTraitementResponse.builder()
                .idacte(TEST_ID)
                .type_element(updateRequest.getType_element())
                .commentaire(updateRequest.getCommentaire())
                .quantite(updateRequest.getQuantite())
                .caseId(TEST_CASE_ID)
                .prestationId(String.valueOf(TEST_PRESTATION_ID))
                .build();
        
        when(acteTraitementService.updateActeTraitement(eq(TEST_ID), any(ActeTraitementUpdateRequest.class)))
            .thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/api/acte-traitements/{id}", TEST_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idacte", is(TEST_ID)))
                .andExpect(jsonPath("$.type_element", is(updateRequest.getType_element())))
                .andExpect(jsonPath("$.commentaire", is(updateRequest.getCommentaire())));
    }

    @Test
    @DisplayName("DELETE /api/acte-traitements/{id} - Success")
    public void testDeleteActeTraitement() throws Exception {
        // Arrange
        doNothing().when(acteTraitementService).deleteActeTraitement(TEST_ID);

        // Act & Assert
        mockMvc.perform(delete("/api/acte-traitements/{id}", TEST_ID))
                .andExpect(status().isNoContent());
    }

    /**
     * Helper method to create a sample ActeTraitement request
     */
    private ActeTraitementRequest createSampleRequest() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        Date currentDate = new Date();
        Date futureDate = new Date(currentDate.getTime() + 86400000); // +1 day

        return ActeTraitementRequest.builder()
                .idacte(TEST_ID)
                .refTacheBPU("REF-123")
                .prestationId(TEST_PRESTATION_ID)
                .type_element("TestElement")
                .quantite(5)
                .dateReception(currentDate)
                .dateLivraison(futureDate)
                .affectation("User1")
                .duree(10)
                .commentaire("Test Comment")
                .motif("Test Motif")
                .statutFacturation("PENDING")
                .priorite("P1")
                .tarifId(TEST_TARIF_ID)
                .classification("Test Classification")
                .userLastName("Doe")
                .userFirstName("John")
                .userRole("ADMIN")
                .userTeam("TUNISIA")
                .caseName("Test Case")
                .caseDescription("Test Case Description")
                .caseType("GRAFIC")
                .build();
    }

    /**
     * Helper method to create a sample ActeTraitement update request
     */
    private ActeTraitementUpdateRequest createSampleUpdateRequest() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        Date currentDate = new Date();
        Date futureDate = new Date(currentDate.getTime() + 86400000); // +1 day

        return ActeTraitementUpdateRequest.builder()
                .prestationId(TEST_PRESTATION_ID)
                .type_element("Updated Element")
                .quantite(10)
                .dateReception(currentDate)
                .dateLivraison(futureDate)
                .affectation("User2")
                .duree(15)
                .commentaire("Updated Comment")
                .motif("Updated Motif")
                .statutFacturation("COMPLETED")
                .priorite("P2")
                .tarifId(TEST_TARIF_ID)
                .classification("Updated Classification")
                .userLastName("Smith")
                .userFirstName("Jane")
                .userRole("MANAGER")
                .userTeam("MOROCCO")
                .build();
    }

    /**
     * Helper method to create a sample ActeTraitement response
     */
    private ActeTraitementResponse createSampleResponse(String id, ActeTraitementRequest request) {
        return ActeTraitementResponse.builder()
                .idacte(id)
                .refTacheBPU(request.getRefTacheBPU())
                .prestationId(String.valueOf(request.getPrestationId()))
                .prestationName("Test Prestation")
                .type_element(request.getType_element())
                .quantite(request.getQuantite())
                .dateReception(request.getDateReception())
                .dateLivraison(request.getDateLivraison())
                .affectation(request.getAffectation())
                .duree(request.getDuree())
                .commentaire(request.getCommentaire())
                .motif(request.getMotif())
                .statutFacturation(request.getStatutFacturation())
                .priorite(request.getPriorite())
                .tarifId(request.getTarifId())
                .tarifName("Test Tarif")
                .classification(request.getClassification())
                .caseId(TEST_CASE_ID)
                .caseName("Test Case")
                .isDeleted(false)
                .build();
    }
}