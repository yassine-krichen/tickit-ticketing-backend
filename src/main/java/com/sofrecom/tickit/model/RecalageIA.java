package com.sofrecom.tickit.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
public class RecalageIA extends Acte_traitement{
    private String idTache;
    private String codeZRC;
    private String zone;
    private String commune;
    private String code_insee;
    private String priorite_rec;
    private Double metre_effectue;
    private String statut_operationnel;
    @Column(name = "metre_non_recale")
    private Double metre_nonrecale;
    @Temporal(TemporalType.DATE)
    private Date date_attribution;
    @Temporal(TemporalType.DATE)
    private Date date_liberation;
    @Temporal(TemporalType.DATE)
    private Date date_demande_injection;

    public RecalageIA() {
        super();
    }

    public String getIdTache() {
        return idTache;
    }

    public void setIdTache(String idTache) {
        this.idTache = idTache;
    }

    public String getCodeZRC() {
        return codeZRC;
    }

    public void setCodeZRC(String codeZRC) {
        this.codeZRC = codeZRC;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getCommune() {
        return commune;
    }

    public void setCommune(String commune) {
        this.commune = commune;
    }

    public String getCode_insee() {
        return code_insee;
    }

    public void setCode_insee(String code_insee) {
        this.code_insee = code_insee;
    }

    public String getPriorite_rec() {
        return priorite_rec;
    }

    public void setPriorite_rec(String priorite_rec) {
        this.priorite_rec = priorite_rec;
    }

    public Double getMetre_effectue() {
        return metre_effectue;
    }

    public void setMetre_effectue(Double metre_effectue) {
        this.metre_effectue = metre_effectue;
    }

    public String getStatut_operationnel() {
        return statut_operationnel;
    }

    public void setStatut_operationnel(String statut_operationnel) {
        this.statut_operationnel = statut_operationnel;
    }

    public Double getMetre_nonrecale() {
        return metre_nonrecale;
    }

    public void setMetre_nonrecale(Double metre_nonrecale) {
        this.metre_nonrecale = metre_nonrecale;
    }

    public Date getDate_attribution() {
        return date_attribution;
    }

    public void setDate_attribution(Date date_attribution) {
        this.date_attribution = date_attribution;
    }

    public Date getDate_liberation() {
        return date_liberation;
    }

    public void setDate_liberation(Date date_liberation) {
        this.date_liberation = date_liberation;
    }

    public Date getDate_demande_injection() {
        return date_demande_injection;
    }

    public void setDate_demande_injection(Date date_demande_injection) {
        this.date_demande_injection = date_demande_injection;
    }
}
