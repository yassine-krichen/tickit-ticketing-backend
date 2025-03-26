package com.sofrecom.tickit.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.Set;

@Entity
public class Nropm extends Acte_traitement{

    String uniteIntervention;
    String cog;
    int nbreFI;

    Date dateReafectation;

    Date dateLivraisonReaffectation;

    String motifReaffectation;

    String typeActivite;


    @JsonManagedReference
    @OneToMany(mappedBy="nropm", fetch = FetchType.LAZY)
    private Set<Pm> pms;


    public Nropm(Prestation type_prestation, String type_element, int quantite, String statutFacturation, String motif,Date dateReception,Date dateDeadline, String cog) {
        super(type_prestation, type_element, quantite, statutFacturation,motif,dateReception,dateDeadline);
        this.cog = cog;
    }


    //Historique
    public Nropm(Prestation type_prestation, String type_element, int quantite, Date dateReception, Date dateLivraison, Date dateValidation, String affectation, int duree, String commentaire, String motif, String statutFacturation,Date dateDeadline,String priorite,String classification, String uniteIntervention, String cog, Date dateReafectation, Date dateLivraisonReaffectation, String motifReaffectation, int nbreFI) {
        super(type_prestation, type_element, quantite, dateReception, dateLivraison, dateValidation, affectation, duree, commentaire, motif,statutFacturation,dateDeadline,priorite, classification);
        this.uniteIntervention = uniteIntervention;
        this.cog = cog;
        this.dateReafectation = dateReafectation;
        this.dateLivraisonReaffectation = dateLivraisonReaffectation;
        this.motifReaffectation = motifReaffectation;
        this.nbreFI= nbreFI;
    }

    //SPI
    public Nropm(Prestation type_prestation, String type_element, int quantite, String statutFacturation, String motif,Date dateReception,Date dateDeadline, String cog, String ui, String typeActivite) {
        super(type_prestation, type_element, quantite, statutFacturation,motif,dateReception,dateDeadline);
        this.cog = cog;
        this.uniteIntervention = ui;
        this.typeActivite= typeActivite;
    }

    public Nropm(Prestation type_prestation, String type_element, int quantite, Date dateReception, Date dateLivraison, Date dateValidation, String affectation, int duree, String commentaire, String motif, String statutFacturation,Date dateDeadline,String priorite,String classification, String uniteIntervention, String cog, Date dateReafectation, Date dateLivraisonReaffectation, String motifReaffectation, String typeActivite) {
        super(type_prestation, type_element, quantite, dateReception, dateLivraison, dateValidation, affectation, duree, commentaire, motif,statutFacturation,dateDeadline,priorite, classification);
        this.uniteIntervention = uniteIntervention;
        this.cog = cog;
        this.dateReafectation = dateReafectation;
        this.dateLivraisonReaffectation = dateLivraisonReaffectation;
        this.motifReaffectation = motifReaffectation;
        this.typeActivite = typeActivite;

    }

    public Nropm(String refTacheBPU, Prestation type_prestation, String type_element, int quantite, Date dateReception, Date dateLivraison, Date dateValidation, String affectation, int duree, String commentaire, String motif, String statutFacturation, Date dateReprise, String repriseFacturable, String uniteIntervention, String cog, Date dateReafectation, Date dateLivraisonReaffectation, String motifReaffectation, String typeActivite) {
        super(refTacheBPU, type_prestation, type_element, quantite, dateReception, dateLivraison, dateValidation, affectation, duree, commentaire, motif, statutFacturation, dateReprise, repriseFacturable);
        this.uniteIntervention = uniteIntervention;
        this.cog = cog;
        this.dateReafectation = dateReafectation;
        this.dateLivraisonReaffectation = dateLivraisonReaffectation;
        this.motifReaffectation = motifReaffectation;
        this.typeActivite = typeActivite;
    }

    public Nropm(){

    }


    public Nropm(String refTacheBPU, Prestation type_prestation, String type_element, int quantite, Date dateReception, Date dateLivraison, Date dateValidation, String affectation, int duree, String commentaire, String motif, String statutFacturation, Date dateReprise, String repriseFacturable, String uniteIntervention, String cog, Date dateReafectation, Date dateLivraisonReaffectation, String motifReaffectation, String typeActivite, Set<Pm> pms) {
        super(refTacheBPU, type_prestation, type_element, quantite, dateReception, dateLivraison, dateValidation, affectation, duree, commentaire, motif, statutFacturation, dateReprise, repriseFacturable);
        this.uniteIntervention = uniteIntervention;
        this.cog = cog;
        this.dateReafectation = dateReafectation;
        this.dateLivraisonReaffectation = dateLivraisonReaffectation;
        this.motifReaffectation = motifReaffectation;
        this.typeActivite = typeActivite;
        this.pms = pms;
    }


    // Constructeur d'ajout
    public Nropm(Prestation type_prestation, String type_element, int quantite, Date dateReception, Date dateLivraison, Date dateValidation, String affectation, int duree, String commentaire, String motif, String statutFacturation,Date dateDeadline,String priorite, String classification, String uniteIntervention, String cog, int nbreFI,  String typeActivite, Set<Pm> pms) {
        super(type_prestation, type_element, quantite, dateReception, dateLivraison, dateValidation, affectation, duree, commentaire, motif, statutFacturation,dateDeadline,priorite, classification);
        this.uniteIntervention = uniteIntervention;
        this.cog = cog;
        this.nbreFI= nbreFI;
        this.typeActivite= typeActivite;
        this.pms = pms;
    }
    //constru de filtrage
    public Nropm(Prestation type_prestation, String type_element,Tarification tarif, int quantite, Date dateReception, Date dateLivraison, Date dateValidation, String affectation, int duree, String commentaire, String motif, String statutFacturation,Date dateDeadline,String priorite,String classification, String uniteIntervention, String cog, int nbreFI,  String typeActivite, Set<Pm> pms) {
        super(type_prestation, type_element,tarif, quantite, dateReception, dateLivraison, dateValidation, affectation, duree, commentaire, motif, statutFacturation,dateDeadline,priorite, classification);
        this.uniteIntervention = uniteIntervention;
        this.cog = cog;
        this.nbreFI= nbreFI;
        this.typeActivite= typeActivite;
        // this.pms = pms;
    }


    // Constructeur de reaff
    public Nropm(Prestation type_prestation, String type_element, int quantite, Date dateReception, Date dateLivraison, Date dateValidation, String affectation, int duree, String commentaire, String motif, String statutFacturation,Date dateDeadline,String priorite,String classification, String uniteIntervention, String cog, int nbreFI,  String typeActivite, Set<Pm> pms, String motifReaffectation) {
        super(type_prestation, type_element, quantite, dateReception, dateLivraison, dateValidation, affectation, duree, commentaire, motif, statutFacturation,dateDeadline,priorite, classification);
        this.uniteIntervention = uniteIntervention;
        this.cog = cog;
        this.nbreFI= nbreFI;
        this.typeActivite= typeActivite;
        this.pms = pms;
        this.motifReaffectation = motifReaffectation;
    }



    public Nropm(String refTacheBPU, Prestation type_prestation, String type_element, int quantite, Date dateReception,
                 Date dateLivraison, Date dateValidation, String affectation, int duree, String commentaire, String motif,
                 String uniteIntervention, String cog, int nbreFI, Date dateReafectation,
                 Date dateLivraisonReaffectation, String motifReaffectation, String typeActivite) {
        super(refTacheBPU, type_prestation, type_element, quantite, dateReception, dateLivraison, dateValidation,
                affectation, duree, commentaire, motif);
        this.uniteIntervention = uniteIntervention;
        this.cog = cog;
        this.nbreFI = nbreFI;
        this.dateReafectation = dateReafectation;
        this.dateLivraisonReaffectation = dateLivraisonReaffectation;
        this.motifReaffectation = motifReaffectation;
        this.typeActivite = typeActivite;
    }
    //spi
    public Nropm(String newCog, String motifNropm, String typeElement, Prestation pres, int i, String enCoursCds, Date dateReception, Date dateDeadline, String ui) {
    }

    public String getUniteIntervention() {
        return uniteIntervention;
    }

    public void setUniteIntervention(String uniteIntervention) {
        this.uniteIntervention = uniteIntervention;
    }

    public String getCog() {
        return cog;
    }

    public void setCog(String cog) {
        this.cog = cog;
    }


    public int getNbreFI() {
        return nbreFI;
    }

    public void setNbreFI(int nbreFI) {
        this.nbreFI = nbreFI;
    }





    public Date getDateReafectation() {
        return dateReafectation;
    }

    public void setDateReafectation(Date dateReafectation) {
        this.dateReafectation = dateReafectation;
    }

    public Date getDateLivraisonReaffectation() {
        return dateLivraisonReaffectation;
    }

    public void setDateLivraisonReaffectation(Date dateLivraisonReaffectation) {
        this.dateLivraisonReaffectation = dateLivraisonReaffectation;
    }

    public String getMotifReaffectation() {
        return motifReaffectation;
    }

    public void setMotifReaffectation(String motifReaffectation) {
        this.motifReaffectation = motifReaffectation;
    }


    public Set<Pm> getPms() {
        return pms;
    }

    public void setPms(Set<Pm> pms) {
        this.pms = pms;
    }


    public String getTypeActivite() {
        return typeActivite;
    }


    public void setTypeActivite(String typeActivite) {
        this.typeActivite = typeActivite;
    }




}
