package com.sofrecom.tickit.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

/**
 * Response Data Transfer Object for Acte_traitement.
 * Used for transferring treatment act data from server to client.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActeTraitementResponse {
    
    private String idacte;
    private String refTacheBPU;
    
    private String prestationId;
    private String prestationName;
    
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
    private String caseName;
    private boolean isDeleted;
}