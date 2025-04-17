package com.sofrecom.tickit.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

/**
 * Data Transfer Object for Acte_traitement.
 * Used for transferring treatment act data between client and server.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActeTraitementDTO {
    
    private String idacte;
    private String refTacheBPU;
    private String prestationId;  // ID of the prestation
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
    private String tarifId;  // ID of the tarification
    private String classification;
    private UUID caseId;  // Optional, may be null for new cases
    
    // Case details for auto-creating a case when needed
    private String caseName;
    private String caseDescription;
    private String caseType;  // Will be converted to CaseType enum
    private String caseStatus;  // Will be converted to Case.Status enum
    private String casePriority;  // Will be converted to Case.Priority enum
    private String caseTeam;  // Will be converted to Case.Team enum
}