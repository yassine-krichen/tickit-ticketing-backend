package com.sofrecom.tickit.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

/**
 * Enhanced response DTO for treatment acts that includes populated case and prestation data.
 * Contains all fields from ActeTraitementResponse plus additional populated fields.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PopulatedTreatmentActResponse {
    
    // Basic treatment act fields
    private String idacte;
    private String refTacheBPU;
    private String prestationId;
    private String type_element;
    private int quantite;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateReception;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateLivraison;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateValidation;
    
    private String affectation;
    private int duree;
    private String commentaire;
    private String motif;
    private String statutFacturation;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateReprise;
    
    private String repriseFacturable;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateDeadline;
    
    private String priorite;
    private String tarifId;
    private String tarifName;
    private String classification;
    private UUID caseId;
    private boolean isDeleted;
    
    // Enhanced case information fields
    private String caseName;
    private String caseStatus;
    private String caseType;
    private String casePriority;
    
    // Enhanced prestation information
    private String prestationName;
    
    /**
     * Static factory method to create a populated response from a standard response
     * 
     * @param baseResponse The base ActeTraitementResponse object
     * @return A new PopulatedTreatmentActResponse with data from the base response
     */
    public static PopulatedTreatmentActResponse fromBaseResponse(ActeTraitementResponse baseResponse) {
        if (baseResponse == null) {
            return null;
        }
        
        return PopulatedTreatmentActResponse.builder()
                .idacte(baseResponse.getIdacte())
                .refTacheBPU(baseResponse.getRefTacheBPU())
                .prestationId(baseResponse.getPrestationId())
                .prestationName(baseResponse.getPrestationName())
                .type_element(baseResponse.getType_element())
                .quantite(baseResponse.getQuantite())
                .dateReception(baseResponse.getDateReception())
                .dateLivraison(baseResponse.getDateLivraison())
                .dateValidation(baseResponse.getDateValidation())
                .affectation(baseResponse.getAffectation())
                .duree(baseResponse.getDuree())
                .commentaire(baseResponse.getCommentaire())
                .motif(baseResponse.getMotif())
                .statutFacturation(baseResponse.getStatutFacturation())
                .dateReprise(baseResponse.getDateReprise())
                .repriseFacturable(baseResponse.getRepriseFacturable())
                .dateDeadline(baseResponse.getDateDeadline())
                .priorite(baseResponse.getPriorite())
                .tarifId(baseResponse.getTarifId())
                .tarifName(baseResponse.getTarifName())
                .classification(baseResponse.getClassification())
                .caseId(baseResponse.getCaseId())
                .caseName(baseResponse.getCaseName())
                .isDeleted(baseResponse.isDeleted())
                .build();
    }
}