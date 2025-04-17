package com.sofrecom.tickit.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

/**
 * Request Data Transfer Object for Acte_traitement.
 * Used for transferring treatment act data between client and server during creation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActeTraitementRequest {
    
    private String idacte;
    @Builder.Default
    private String refTacheBPU = "DEFAULT_REF"; // Default value as it's not significant
    private int prestationId;  // ID of the prestation
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
    
    // User information (temporary until auth is implemented)
    private String userLastName;
    private String userFirstName;
    private String userRole;
    private String userTeam;
    
    // Case details for auto-creating a case when needed
    private String caseName;
    private String caseDescription;
    private String caseType;  // Will be converted to CaseType enum
}