package com.sofrecom.tickit.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Update Request Data Transfer Object for Acte_traitement.
 * Used for transferring treatment act update data from client to server.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActeTraitementUpdateRequest {
    
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
    
    // User information (temporary until auth is implemented)
    private String userLastName;
    private String userFirstName;
    private String userRole;
    private String userTeam;
}