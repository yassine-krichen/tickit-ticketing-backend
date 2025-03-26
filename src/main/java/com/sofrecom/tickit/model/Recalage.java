package com.sofrecom.tickit.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
public class Recalage extends Acte_traitement{

    private String idTache;
    private String zone;
    private String commune;
    private String code_insee;
    private String statut_operationnel;

    private Double metre_effectue;
    @Column(name = "metre_non_recale")
    private Double metre_nonrecale;
    @Temporal(TemporalType.DATE)
    private Date date_attribution;

    @Temporal(TemporalType.DATE)
    private Date date_demande_injection;
    public Recalage() {
        super();
    }



    public String getIdTache() {
        return idTache;
    }

    public void setIdTache(String idTache) {
        this.idTache = idTache;
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

    public String getStatut_operationnel() {
        return statut_operationnel;
    }

    public void setStatut_operationnel(String statut_operationnel) {
        this.statut_operationnel = statut_operationnel;
    }



    public Double getMetre_effectue() {
        return metre_effectue;
    }

    public void setMetre_effectue(Double metre_effectue) {
        this.metre_effectue = metre_effectue;
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



    public Date getDate_demande_injection() {
        return date_demande_injection;
    }

    public void setDate_demande_injection(Date date_demande_injection) {
        this.date_demande_injection = date_demande_injection;
    }

    public Recalage(Date dateReception, Date dateLivraison, Date dateValidation, String affectation, int duree, String commentaire, String motif, String statutFacturation, Date dateDeadline, String priorite, String idTache, String zone, String commune, String code_insee, String statut_operationnel, Double metre_effectue, Double metre_nonrecale, Date date_attribution, Date date_demande_injection) {
        super(dateReception, dateLivraison, dateValidation, affectation, duree, commentaire, motif, statutFacturation, dateDeadline, priorite);
        this.idTache = idTache;
        this.zone = zone;
        this.commune = commune;
        this.code_insee = code_insee;
        this.statut_operationnel = statut_operationnel;
        this.metre_effectue = metre_effectue;
        this.metre_nonrecale = metre_nonrecale;
        this.date_attribution = date_attribution;
        this.date_demande_injection = date_demande_injection;
    }

    public Recalage(String idacte, String refTacheBPU, Prestation type_prestation, String type_element, int quantite, Date dateReception, Date dateLivraison, Date dateValidation, String affectation, int duree, String commentaire, String motif, String statutFacturation, Date dateReprise, String repriseFacturable, Date dateDeadline, String priorite, Tarification tarif, String idTache, String zone, String commune, String code_insee, String statut_operationnel, Double metre_effectue, Double metre_nonrecale, Date date_attribution, Date date_demande_injection) {
        super(idacte, refTacheBPU, type_prestation, type_element, quantite, dateReception, dateLivraison, dateValidation, affectation, duree, commentaire, motif, statutFacturation, dateReprise, repriseFacturable, dateDeadline, priorite, tarif);
        this.idTache = idTache;
        this.zone = zone;
        this.commune = commune;
        this.code_insee = code_insee;
        this.statut_operationnel = statut_operationnel;
        this.metre_effectue = metre_effectue;
        this.metre_nonrecale = metre_nonrecale;
        this.date_attribution = date_attribution;
        this.date_demande_injection = date_demande_injection;
    }

}
