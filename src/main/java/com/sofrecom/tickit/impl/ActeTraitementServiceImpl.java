package com.sofrecom.tickit.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sofrecom.tickit.dto.ActeTraitementRequest;
import com.sofrecom.tickit.dto.ActeTraitementResponse;
import com.sofrecom.tickit.dto.ActeTraitementUpdateRequest;
import com.sofrecom.tickit.dto.PopulatedTreatmentActResponse;
import com.sofrecom.tickit.exception.ResourceNotFoundException;
import com.sofrecom.tickit.model.*;
import com.sofrecom.tickit.repository.ActeTraitementRepository;
import com.sofrecom.tickit.repository.CaseRepository;
import com.sofrecom.tickit.repository.PrestationRepository;
import com.sofrecom.tickit.repository.TarificationRepository;
import com.sofrecom.tickit.service.ActeTraitementService;
import com.sofrecom.tickit.service.LedgerService;
// import com.sofrecom.tickit.util.CryptoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the ActeTraitementService interface.
 * Manages business logic for treatment acts, cases, and blockchain ledger entries.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ActeTraitementServiceImpl implements ActeTraitementService {

    private final ActeTraitementRepository acteTraitementRepository;
    private final CaseRepository caseRepository;
    private final PrestationRepository prestationRepository;
    private final TarificationRepository tarificationRepository;
    private final LedgerService ledgerService;
    // private final CryptoUtil cryptoUtil;
    private final ObjectMapper objectMapper;

    /**
     * Create a new treatment act.
     * If a case ID is provided, the act is associated with an existing case;
     * otherwise, a new case is created.
     *
     * @param request Treatment act request
     * @return Created treatment act response
     */
    @Override
    @Transactional
    public ActeTraitementResponse createActeTraitement(ActeTraitementRequest request) {
        // Find or create the case
        Case associatedCase;
        
        if (request.getCaseId() != null) {
            associatedCase = caseRepository.findById(request.getCaseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Case", "Id", request.getCaseId()));
        } else {
            // Create a new case
            associatedCase = createNewCase(request);
            
            // Create ledger entry for the new case
            createCaseLedgerEntry(associatedCase, LedgerEntry.ActionType.CREATE, generateUserFingerprint(request));
        }

        // Create the treatment act
        Acte_traitement acteTraitement = new Acte_traitement();
        updateActeTraitementFromRequest(acteTraitement, request);

        // Associate with case
        acteTraitement.setAssociatedCase(associatedCase);
        
        // Save entity
        acteTraitement = acteTraitementRepository.save(acteTraitement);
        
        // Create ledger entry for the new treatment act
        createLedgerEntry(acteTraitement, LedgerEntry.ActionType.CREATE, generateUserFingerprint(request));
        
        return mapToResponse(acteTraitement);
    }

    /**
     * Retrieve a treatment act by its ID.
     *
     * @param id Treatment act ID
     * @return Found treatment act response or throws exception if not found
     */
    @Override
    public ActeTraitementResponse getActeTraitementById(String id) {
        Acte_traitement acteTraitement = acteTraitementRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment act", "Id", id));
        return mapToResponse(acteTraitement);
    }

    /**
     * Get a treatment act by ID with populated case and prestation data.
     * 
     * @param id The treatment act ID
     * @return A populated treatment act response with detailed case and prestation information
     */
    @Override
    public PopulatedTreatmentActResponse getPopulatedActeTraitementById(String id) {
        // Get the basic treatment act first
        Acte_traitement acteTraitement = acteTraitementRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment act", "Id", id));
        
        // Create the base response
        ActeTraitementResponse baseResponse = mapToResponse(acteTraitement);
        
        // Create the populated response and extend it with additional data
        PopulatedTreatmentActResponse populatedResponse = PopulatedTreatmentActResponse.fromBaseResponse(baseResponse);
        
        // Populate case information if available
        Case associatedCase = acteTraitement.getAssociatedCase();
        if (associatedCase != null) {
            populatedResponse.setCaseName(associatedCase.getName());
            populatedResponse.setCaseStatus(associatedCase.getStatus().toString());
            populatedResponse.setCaseType(associatedCase.getType().getValue());
            populatedResponse.setCasePriority(associatedCase.getPriority().toString());
        }
        
        // Populate prestation information if available
        if (acteTraitement.getType_prestation() != null) {
            populatedResponse.setPrestationName(acteTraitement.getType_prestation().getNomPrestation());
        }
        
        return populatedResponse;
    }

    /**
     * Retrieve all active treatment acts.
     *
     * @return List of all active treatment acts responses
     */
    @Override
    public List<ActeTraitementResponse> getAllActeTraitements() {
        return acteTraitementRepository.findAllActive().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieve all treatment acts associated with a specific case.
     *
     * @param caseId Case identifier
     * @return List of treatment acts responses belonging to the case
     */
    @Override
    public List<ActeTraitementResponse> getActeTraitementsByCaseId(UUID caseId) {
        return acteTraitementRepository.findByCaseId(caseId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update an existing treatment act.
     * Records the update operation in the blockchain ledger.
     *
     * @param id Treatment act ID
     * @param updateRequest Treatment act update request
     * @return Updated treatment act response
     */
    @Override
    @Transactional
    public ActeTraitementResponse updateActeTraitement(String id, ActeTraitementUpdateRequest updateRequest) {
        Acte_traitement acteTraitement = acteTraitementRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment act", "Id", id));
        
        // Update fields
        updateActeTraitementFromUpdateRequest(acteTraitement, updateRequest);
        
        // Update case's status and updatedAt
        if (acteTraitement.getAssociatedCase() != null) {
            Case associatedCase = acteTraitement.getAssociatedCase();
            associatedCase.setUpdatedAt(LocalDateTime.now());
            caseRepository.save(associatedCase);
        }
        
        // Save changes
        acteTraitement = acteTraitementRepository.save(acteTraitement);
        
        // Create ledger entry for the update
        String userFingerprint = generateUserFingerprint(
                updateRequest.getUserLastName(),
                updateRequest.getUserFirstName(),
                updateRequest.getUserRole(),
                updateRequest.getUserTeam()
        );
        createLedgerEntry(acteTraitement, LedgerEntry.ActionType.UPDATE, userFingerprint);
        
        return mapToResponse(acteTraitement);
    }

    /**
     * Soft delete a treatment act.
     * Records the delete operation in the blockchain ledger.
     *
     * @param id Treatment act ID
     */
    @Override
    @Transactional
    public void deleteActeTraitement(String id) {
        Acte_traitement acteTraitement = acteTraitementRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment act", "Id", id));
        
        // Get user information before deletion (simplified for now)
        // TODO: change when you implement auth to use the actual userFingerprint instead
        String userFingerprint = "system.delete.admin.system";
        
        // Soft delete by marking as deleted
        acteTraitement.setDeleted(true);
        acteTraitementRepository.save(acteTraitement);
        
        // Create ledger entry for the delete
        createLedgerEntry(acteTraitement, LedgerEntry.ActionType.DELETE, userFingerprint);
    }

    /**
     * Get the number of treatment acts (steps) in a case.
     *
     * @param caseId Case identifier
     * @return Number of treatment acts (CREATE actions) for the case
     */
    @Override
    public int getNumberOfStepsByCaseId(UUID caseId) {
        // TODO: we can move this to somewhere else if needed
        List<LedgerEntry> createEntries = ledgerService.getLedgerForCase(
                caseId.toString(), 
                "TREATMENT_ACT"
        ).stream()
        .filter(entry -> entry.getActionType() == LedgerEntry.ActionType.CREATE)
        .collect(Collectors.toList());
        
        return createEntries.size();
    }

    /**
     * Create a new case based on treatment act data.
     *
     * @param request Treatment act request
     * @return Newly created case
     */
    private Case createNewCase(ActeTraitementRequest request) {
        CaseType caseType;
        try {
            caseType = request.getCaseType() != null ? 
                    CaseType.valueOf(request.getCaseType()) : CaseType.BRAME;
        } catch (Exception e) {
            // Default to BRAME if invalid
            caseType = CaseType.BRAME;
        }
        
        // Auto-generate case name based on treatment act info if not provided
        String caseName = request.getCaseName();
        if (caseName == null || caseName.trim().isEmpty()) {
            caseName = generateCaseName(request, caseType);
        }
        
        // Use provided description or default
        String caseDescription = request.getCaseDescription();
        if (caseDescription == null || caseDescription.trim().isEmpty()) {
            caseDescription = "N/A";
        }
        
        // Create case with default values
        Case newCase = Case.builder()
                .id(UUID.randomUUID())
                .name(caseName)
                .description(caseDescription)
                .type(caseType)
                .status(Case.Status.ONGOING)  // Default to ONGOING for new cases
                .priority(Case.Priority.MEDIUM)  // Default priority
                .team(determineTeam(request.getUserTeam()))
                .createdAt(LocalDateTime.now())
                .createdBy(generateUserFingerprint(request))
                .updatedAt(LocalDateTime.now())
                .tags(new ArrayList<>())
                .build();
        
        return caseRepository.save(newCase);
    }

    /**
     * Generate a case name based on treatment act information.
     *
     * @param request Treatment act request
     * @param caseType Case type
     * @return Generated case name
     */
    private String generateCaseName(ActeTraitementRequest request, CaseType caseType) {
        StringBuilder nameBuilder = new StringBuilder();
        nameBuilder.append(caseType.getValue()).append(" - ");
        
        // Add date information if available
        if (request.getDateReception() != null) {
            nameBuilder.append(request.getDateReception()).append(" - ");
        }
        
        // Add classification or type if available
        if (request.getClassification() != null && !request.getClassification().isEmpty()) {
            nameBuilder.append(request.getClassification()).append(" - ");
        } else if (request.getType_element() != null && !request.getType_element().isEmpty()) {
            nameBuilder.append(request.getType_element()).append(" - ");
        }
        
        // Add user info
        nameBuilder.append("Created by ").append(request.getUserFirstName())
                .append(" ").append(request.getUserLastName());
        
        return nameBuilder.toString();
    }

    /**
     * Determine the team enum from string representation.
     *
     * @param teamString Team string
     * @return Team enum value
     */
    private Case.Team determineTeam(String teamString) {
        if (teamString != null) {
            try {
                return Case.Team.valueOf(teamString.toUpperCase());
            } catch (IllegalArgumentException e) {
                // If not a valid team, log and use default
                log.warn("Invalid team value: {}. Using default TUNISIA.", teamString);
            }
        }
        return Case.Team.TUNISIA;  // Default
    }

    /**
     * Update a treatment act entity from request data.
     *
     * @param acteTraitement Treatment act entity to update
     * @param request Request with new values
     */
    private void updateActeTraitementFromRequest(Acte_traitement acteTraitement, ActeTraitementRequest request) {
        // Only set ID for new entities
        if (acteTraitement.getIdacte() == null) {
            acteTraitement.setIdacte(request.getIdacte() != null ? 
                    request.getIdacte() : UUID.randomUUID().toString());
        }
        
        acteTraitement.setRefTacheBPU(request.getRefTacheBPU());
        
        // Set prestation if provided
        if (request.getPrestationId() > 0) {
            Prestation prestation = prestationRepository.findById(request.getPrestationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Prestation", "Id", request.getPrestationId()));
            acteTraitement.setType_prestation(prestation);
        }
        
        acteTraitement.setType_element(request.getType_element());
        acteTraitement.setQuantite(request.getQuantite());
        acteTraitement.setDateReception(request.getDateReception());
        acteTraitement.setDateLivraison(request.getDateLivraison());
        acteTraitement.setDateValidation(request.getDateValidation());
        acteTraitement.setAffectation(request.getAffectation());
        acteTraitement.setDuree(request.getDuree());
        acteTraitement.setCommentaire(request.getCommentaire());
        acteTraitement.setMotif(request.getMotif());
        acteTraitement.setStatutFacturation(request.getStatutFacturation());
        acteTraitement.setDateReprise(request.getDateReprise());
        acteTraitement.setRepriseFacturable(request.getRepriseFacturable());
        acteTraitement.setDateDeadline(request.getDateDeadline());
        acteTraitement.setPriorite(request.getPriorite());
        acteTraitement.setClassification(request.getClassification());
        acteTraitement.setDeleted(false); // Ensure new entities are not deleted
        
        // Set tarification if provided
        if (request.getTarifId() != null) {
            Tarification tarification = tarificationRepository.findById(request.getTarifId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tarification", "Id", request.getTarifId()));
            acteTraitement.setTarif(tarification);
        }
    }

    /**
     * Update a treatment act entity from update request.
     *
     * @param acteTraitement Treatment act entity to update
     * @param updateRequest Update request with new values
     */
    private void updateActeTraitementFromUpdateRequest(Acte_traitement acteTraitement, ActeTraitementUpdateRequest updateRequest) {
        // Set prestation if provided
        if (updateRequest.getPrestationId() > 0) {
            Prestation prestation = prestationRepository.findById(updateRequest.getPrestationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Prestation", "Id", updateRequest.getPrestationId()));
            acteTraitement.setType_prestation(prestation);
        }
        
        // Note: We don't update case ID from update requests
        
        if (updateRequest.getType_element() != null) {
            acteTraitement.setType_element(updateRequest.getType_element());
        }
        
        acteTraitement.setQuantite(updateRequest.getQuantite());
        
        if (updateRequest.getDateReception() != null) {
            acteTraitement.setDateReception(updateRequest.getDateReception());
        }
        
        if (updateRequest.getDateLivraison() != null) {
            acteTraitement.setDateLivraison(updateRequest.getDateLivraison());
        }
        
        if (updateRequest.getDateValidation() != null) {
            acteTraitement.setDateValidation(updateRequest.getDateValidation());
        }
        
        if (updateRequest.getAffectation() != null) {
            acteTraitement.setAffectation(updateRequest.getAffectation());
        }
        
        acteTraitement.setDuree(updateRequest.getDuree());
        
        if (updateRequest.getCommentaire() != null) {
            acteTraitement.setCommentaire(updateRequest.getCommentaire());
        }
        
        if (updateRequest.getMotif() != null) {
            acteTraitement.setMotif(updateRequest.getMotif());
        }
        
        if (updateRequest.getStatutFacturation() != null) {
            acteTraitement.setStatutFacturation(updateRequest.getStatutFacturation());
        }
        
        if (updateRequest.getDateReprise() != null) {
            acteTraitement.setDateReprise(updateRequest.getDateReprise());
        }
        
        if (updateRequest.getRepriseFacturable() != null) {
            acteTraitement.setRepriseFacturable(updateRequest.getRepriseFacturable());
        }
        
        if (updateRequest.getDateDeadline() != null) {
            acteTraitement.setDateDeadline(updateRequest.getDateDeadline());
        }
        
        if (updateRequest.getPriorite() != null) {
            acteTraitement.setPriorite(updateRequest.getPriorite());
        }
        
        if (updateRequest.getClassification() != null) {
            acteTraitement.setClassification(updateRequest.getClassification());
        }
        
        // Set tarification if provided
        if (updateRequest.getTarifId() != null) {
            Tarification tarification = tarificationRepository.findById(updateRequest.getTarifId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tarification", "Id", updateRequest.getTarifId()));
            acteTraitement.setTarif(tarification);
        }
    }

    /**
     * Create a blockchain ledger entry for a treatment act operation.
     *
     * @param acteTraitement Treatment act entity
     * @param actionType Type of action (CREATE, UPDATE, DELETE)
     * @param userFingerprint User identifier
     */
    private void createLedgerEntry(Acte_traitement acteTraitement, LedgerEntry.ActionType actionType, String userFingerprint) {
        String caseId = acteTraitement.getAssociatedCase() != null ?
                acteTraitement.getAssociatedCase().getId().toString() : "NONE";
        
        // Create JSON snapshot of current entity state
        JsonNode dataSnapshot = createJsonSnapshot(acteTraitement);
        
        // Create ledger entry
        LedgerEntry ledgerEntry = LedgerEntry.builder()
                .id(UUID.randomUUID())
                .timestamp(LocalDateTime.now())
                .userFingerprint(userFingerprint)
                .actionType(actionType)
                .entityType("TREATMENT_ACT")
                .entityId(acteTraitement.getIdacte())
                .caseId(caseId)
                .dataSnapshot(dataSnapshot)
                .build();
        
        // Use the ledger service to save the entry with proper hash calculation
        ledgerService.saveLedgerEntry(ledgerEntry);
        
        log.info("Created ledger entry {} for treatment act {} with action {}", 
                ledgerEntry.getId(), acteTraitement.getIdacte(), actionType);
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
        
        log.info("Created ledger entry {} for case {} with action {}", 
                ledgerEntry.getId(), caseEntity.getId(), actionType);
    }

    /**
     * Create a JSON representation of the treatment act.
     *
     * @param acteTraitement Treatment act entity
     * @return JsonNode representing the entity state
     */
    private JsonNode createJsonSnapshot(Acte_traitement acteTraitement) {
        ObjectNode node = objectMapper.createObjectNode();
        
        node.put("idacte", acteTraitement.getIdacte());
        node.put("refTacheBPU", acteTraitement.getRefTacheBPU());
        
        if (acteTraitement.getType_prestation() != null) {
            node.put("prestationId", acteTraitement.getType_prestation().getIdPrestation());
            node.put("prestationName", acteTraitement.getType_prestation().getNomPrestation());
        }
        
        node.put("type_element", acteTraitement.getType_element());
        node.put("quantite", acteTraitement.getQuantite());
        
        if (acteTraitement.getDateReception() != null) {
            node.put("dateReception", acteTraitement.getDateReception().toString());
        }
        
        if (acteTraitement.getDateLivraison() != null) {
            node.put("dateLivraison", acteTraitement.getDateLivraison().toString());
        }
        
        if (acteTraitement.getDateValidation() != null) {
            node.put("dateValidation", acteTraitement.getDateValidation().toString());
        }
        
        node.put("affectation", acteTraitement.getAffectation());
        node.put("duree", acteTraitement.getDuree());
        node.put("commentaire", acteTraitement.getCommentaire());
        node.put("motif", acteTraitement.getMotif());
        node.put("statutFacturation", acteTraitement.getStatutFacturation());
        
        if (acteTraitement.getDateReprise() != null) {
            node.put("dateReprise", acteTraitement.getDateReprise().toString());
        }
        
        node.put("repriseFacturable", acteTraitement.getRepriseFacturable());
        
        if (acteTraitement.getDateDeadline() != null) {
            node.put("dateDeadline", acteTraitement.getDateDeadline().toString());
        }
        
        node.put("priorite", acteTraitement.getPriorite());
        node.put("classification", acteTraitement.getClassification());
        node.put("isDeleted", acteTraitement.isDeleted());
        
        if (acteTraitement.getTarif() != null) {
            node.put("tarifId", acteTraitement.getTarif().getCodeTarif());
            node.put("tarifName", acteTraitement.getTarif().getValeur());
        }
        
        if (acteTraitement.getAssociatedCase() != null) {
            node.put("caseId", acteTraitement.getAssociatedCase().getId().toString());
            node.put("caseName", acteTraitement.getAssociatedCase().getName());
            node.put("caseStatus", acteTraitement.getAssociatedCase().getStatus().toString());
        }
        
        return node;
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
     * Generate user fingerprint from request.
     *
     * @param request Treatment act request
     * @return User fingerprint string
     */
    private String generateUserFingerprint(ActeTraitementRequest request) {
        return generateUserFingerprint(
                request.getUserLastName(),
                request.getUserFirstName(),
                request.getUserRole(),
                request.getUserTeam()
        );
    }

    /**
     * Generate user fingerprint from components.
     *
     * @param lastName User last name
     * @param firstName User first name
     * @param role User role
     * @param team User team
     * @return User fingerprint string
     */
    private String generateUserFingerprint(String lastName, String firstName, String role, String team) {
        // Format: lastname.firstname.role.team
        StringBuilder fingerprint = new StringBuilder();
        
        fingerprint.append(lastName != null ? lastName.toLowerCase() : "unknown")
                .append(".")
                .append(firstName != null ? firstName.toLowerCase() : "unknown")
                .append(".")
                .append(role != null ? role.toLowerCase() : "unknown")
                .append(".")
                .append(team != null ? team.toLowerCase() : "unknown");
        
        return fingerprint.toString();
    }

    /**
     * Map a treatment act entity to response DTO.
     *
     * @param acteTraitement Treatment act entity
     * @return Treatment act response DTO
     */
    private ActeTraitementResponse mapToResponse(Acte_traitement acteTraitement) {
        ActeTraitementResponse response = ActeTraitementResponse.builder()
                .idacte(acteTraitement.getIdacte())
                .refTacheBPU(acteTraitement.getRefTacheBPU())
                .type_element(acteTraitement.getType_element())
                .quantite(acteTraitement.getQuantite())
                .dateReception(acteTraitement.getDateReception())
                .dateLivraison(acteTraitement.getDateLivraison())
                .dateValidation(acteTraitement.getDateValidation())
                .affectation(acteTraitement.getAffectation())
                .duree(acteTraitement.getDuree())
                .commentaire(acteTraitement.getCommentaire())
                .motif(acteTraitement.getMotif())
                .statutFacturation(acteTraitement.getStatutFacturation())
                .dateReprise(acteTraitement.getDateReprise())
                .repriseFacturable(acteTraitement.getRepriseFacturable())
                .dateDeadline(acteTraitement.getDateDeadline())
                .priorite(acteTraitement.getPriorite())
                .classification(acteTraitement.getClassification())
                .isDeleted(acteTraitement.isDeleted())
                .build();
        
        // Add prestation info if available
        if (acteTraitement.getType_prestation() != null) {
            response.setPrestationId(String.valueOf(acteTraitement.getType_prestation().getIdPrestation()));
            response.setPrestationName(acteTraitement.getType_prestation().getNomPrestation());
        }
        
        // Add tarification info if available
        if (acteTraitement.getTarif() != null) {
            response.setTarifId(acteTraitement.getTarif().getCodeTarif());
            // for now, I'll leave it as a string but later change to double
            response.setTarifName(String.valueOf(acteTraitement.getTarif().getValeur()));
        }
        
        // Add case info if available
        if (acteTraitement.getAssociatedCase() != null) {
            response.setCaseId(acteTraitement.getAssociatedCase().getId());
            response.setCaseName(acteTraitement.getAssociatedCase().getName());
        }
        
        return response;
    }
}