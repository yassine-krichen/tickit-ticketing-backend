package com.sofrecom.tickit.model;


import javax.persistence.Entity;
import java.util.Date;

@Entity
public class Interconnexion extends Acte_traitement{

    private String cog;
    private  String ui ;
    private Date dateFlux;
    private Date dateDepBan;
    private Date dateRetourBan;
    private String phase ;
    private  String motifReaffectation;


    public Interconnexion( String cog, String motif, String type_element, String commentaire, Prestation type_prestation,float quantite, String statutFacturation, Date dateReception, Date dateDeadline, String ui ) {
        super(motif, type_element,commentaire,type_prestation, quantite, statutFacturation, dateReception, dateDeadline);
        this.cog = cog;
        this.ui = ui;
    }
    //constru de filtrage
    public Interconnexion(Prestation type_prestation, String type_element,Tarification tarif, float quantite, Date dateReception, Date dateLivraison, Date dateValidation, String affectation, int duree, String commentaire, String motif, String statutFacturation,Date dateDeadline,String priorite,
                          String classification, String ui, String cog, Date dateDepBan, Date dateRetourBan, String phase) {
        super(type_prestation, type_element,tarif, quantite, dateReception, dateLivraison, dateValidation, affectation, duree, commentaire, motif, statutFacturation,dateDeadline,priorite, classification);
        this.ui = ui;
        this.cog = cog;
        this.phase= phase;
    }

    public String getUi() {
        return ui;
    }

    public void setUi(String ui) {
        this.ui = ui;
    }

    public Interconnexion() {

    }

    public String getMotifReaffectation() {
        return motifReaffectation;
    }

    public void setMotifReaffectation(String motifReaffectation) {
        this.motifReaffectation = motifReaffectation;
    }

    public String getCog() {
        return cog;
    }

    public void setCog(String cog) {
        this.cog = cog;
    }

    public Date getDateFlux() {
        return dateFlux;
    }

    public void setDateFlux(Date dateFlux) {
        this.dateFlux = dateFlux;
    }

    public Date getDateDepBan() {
        return dateDepBan;
    }

    public void setDateDepBan(Date dateDepBan) {
        this.dateDepBan = dateDepBan;
    }

    public Date getDateRetourBan() {
        return dateRetourBan;
    }

    public void setDateRetourBan(Date dateRetourBan) {
        this.dateRetourBan = dateRetourBan;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }
}
