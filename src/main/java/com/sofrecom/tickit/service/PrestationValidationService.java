package com.sofrecom.tickit.service;

import com.sofrecom.tickit.model.CaseType;
import com.sofrecom.tickit.repository.ActeTraitementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple service to validate ticket types against prestation IDs.
 * Used specifically for ticket creation validation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PrestationValidationService {
    
    private final ActeTraitementRepository acteTraitementRepository;
    
    // Simple map of Prestation ID to expected CaseType
    private static final Map<Long, CaseType> PRESTATION_TO_CASE_TYPE = initMapping();
    
    private static Map<Long, CaseType> initMapping() {
        Map<Long, CaseType> mapping = new HashMap<>();
        
        // Based on your populateBackend.sql script
        mapping.put(1L, CaseType.GRAFIC);
        mapping.put(31L, CaseType.BRAME);
        mapping.put(48L, CaseType.GEORESO);
        mapping.put(49L, CaseType.BO_RIP);
        mapping.put(10L, CaseType.INTERCONNEXION_RIP_TIERS);
        mapping.put(4L, CaseType.MUTATION);
        mapping.put(11L, CaseType.DOMMAGES);
        mapping.put(20L, CaseType.MISSING_CUIVRE_FIBRE_CLASSIQUE);
        
        // Add specific steps
        mapping.put(27L, CaseType.BRAME_PRE_ETUDE);
        mapping.put(28L, CaseType.BRAME_ETUDE);
        mapping.put(29L, CaseType.BRAME_RACCORDEMENT);
        mapping.put(30L, CaseType.BRAME_CONTROLE_TRAVAUX);
        
        mapping.put(24L, CaseType.INTERCONNEXION_RIP_TIERS_PHASE_1);
        mapping.put(25L, CaseType.INTERCONNEXION_RIP_TIERS_PHASE_2);
        mapping.put(26L, CaseType.INTERCONNEXION_RIP_TIERS_CONTROLE_TRAVAUX);
        
        mapping.put(21L, CaseType.MUTATION_NATNE);
        mapping.put(22L, CaseType.MUTATION_NATHD);
        mapping.put(23L, CaseType.MUTATION_NAT42C);
        
        mapping.put(45L, CaseType.GEORESO_IMB);
        mapping.put(46L, CaseType.GEORESO_SITE_SUPPORT);
        mapping.put(47L, CaseType.GEORESO_CABLE);
        
        mapping.put(50L, CaseType.BO_RIP_GC);
        mapping.put(51L, CaseType.BO_RIP_CABLAGE);
        mapping.put(52L, CaseType.BO_RIP_GC_CABLAGE);
        
        // Dommages steps
        mapping.put(32L, CaseType.AFFECTATION_ET_MISE_EN_CAUSE);
        mapping.put(33L, CaseType.CREANCE);
        mapping.put(34L, CaseType.RAPPEL);
        mapping.put(35L, CaseType.MISE_EN_DEMEURE);
        mapping.put(36L, CaseType.DERNIERE_LETTRE);
        mapping.put(37L, CaseType.MISE_CONTENTIEUX);
        mapping.put(38L, CaseType.DEMANDE_DINFORMATIONS);
        mapping.put(39L, CaseType.CONTESTATION_OU_RECLAMATION);
        mapping.put(40L, CaseType.CLOTURE_DOSSIER);
        
        return mapping;
    }
    
    /**
     * Gets the prestation ID from a treatment act ID by direct database lookup
     */
    public Long getPrestationIdFromTreatmentAct(String treatmentActId) {
        try {
            return acteTraitementRepository.getPrestationIdByTreatmentActId(treatmentActId)
                    .orElse(null);
        } catch (Exception e) {
            log.warn("Could not get prestation ID for treatment act {}: {}", treatmentActId, e.getMessage());
            return null;
        }
    }
    
    /**
     * Validates if ticket type matches the expected type for the prestation
     */
    public boolean isValidTicketType(Long prestationId, CaseType ticketType) {
        if (prestationId == null || ticketType == null) {
            return true; // Skip validation if data is missing
        }
        
        CaseType expectedType = PRESTATION_TO_CASE_TYPE.get(prestationId);
        if (expectedType == null) {
            log.debug("No expected type mapping found for prestation ID {}, allowing any type", prestationId);
            return true; // Allow if no mapping exists (backward compatibility)
        }
        
        boolean isValid = expectedType.equals(ticketType);
        log.debug("Validation for prestation {} and ticket type {}: expected={}, valid={}", 
                 prestationId, ticketType, expectedType, isValid);
        return isValid;
    }
    
    /**
     * Gets error message for type mismatch
     */
    public String getValidationError(Long prestationId, CaseType providedType) {
        CaseType expectedType = PRESTATION_TO_CASE_TYPE.get(prestationId);
        return String.format("Invalid ticket type '%s' for prestation ID %d. Expected type: '%s'", 
                           providedType, prestationId, expectedType);
    }
    
    /**
     * Validates a ticket against its treatment act's prestation
     */
    public ValidationResult validateTicketType(String treatmentActId, CaseType ticketType) {
        if (treatmentActId == null || ticketType == null) {
            return ValidationResult.valid();
        }
        
        Long prestationId = getPrestationIdFromTreatmentAct(treatmentActId);
        if (prestationId == null) {
            log.debug("No prestation found for treatment act {}, skipping validation", treatmentActId);
            return ValidationResult.valid();
        }
        
        if (isValidTicketType(prestationId, ticketType)) {
            return ValidationResult.valid();
        } else {
            return ValidationResult.invalid(getValidationError(prestationId, ticketType));
        }
    }
    
    /**
     * Simple result class for validation
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String errorMessage;
        
        private ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }
        
        public static ValidationResult valid() {
            return new ValidationResult(true, null);
        }
        
        public static ValidationResult invalid(String errorMessage) {
            return new ValidationResult(false, errorMessage);
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
    }
}