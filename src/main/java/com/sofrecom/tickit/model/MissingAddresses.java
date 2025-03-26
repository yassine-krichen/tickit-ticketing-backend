package com.sofrecom.tickit.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "missing_addresses")
public class MissingAddresses extends Acte_traitement {

    @Column(name = "idTach")
    private String idTach;

    @Column(name = "commune")
    private String commune;

    @Column(name = "codeInsee")
    private String codeInsee;

    @Column(name = "codePostal")
    private String codePostal;

    @Column(name = "adresseFibre")
    private String adresseFibre;

    private String motiffreafectation;

    @Column(name = "creationIPE")
    private String creationIPE;

    @Column(name = "codeIMB")
    private String codeIMB;

    @Column(name = "libelle")
    private String libelle;

    @Column(name = "dateCreationIMB")
    @Temporal(TemporalType.DATE)
    private Date dateCreationIMB;

    @Column(name = "statutOperationnels")
    private String statutOperationnels;

    @Column(name = "dateTraitement")
    @Temporal(TemporalType.DATE)
    private Date dateTraitement;


    @ManyToOne
    @JoinColumn(name = "missing_banbou_id")
    @JsonBackReference
    private MissingBANBOU missingBANBOU;

    public String getIdTach() {
        return idTach;
    }

    public void setIdTach(String idTach) {
        this.idTach = idTach;
    }

    public String getStatutOperationnels() {
        return statutOperationnels;
    }

    public void setStatutOperationnels(String statutOperationnels) {
        this.statutOperationnels = statutOperationnels;
    }

    public Date getDateTraitement() {
        return dateTraitement;
    }

    public void setDateTraitement(Date dateTraitement) {
        this.dateTraitement = dateTraitement;
    }

    public String getCommune() {
        return commune;
    }

    public void setCommune(String commune) {
        this.commune = commune;
    }

    public String getCodeInsee() {
        return codeInsee;
    }

    public void setCodeInsee(String codeInsee) {
        this.codeInsee = codeInsee;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }

    public String getAdresseFibre() {
        return adresseFibre;
    }

    public void setAdresseFibre(String adresseFibre) {
        this.adresseFibre = adresseFibre;
    }

    public String getCreationIPE() {
        return creationIPE;
    }

    public void setCreationIPE(String creationIPE) {
        this.creationIPE = creationIPE;
    }
    public String getCodeIMB() {
        return codeIMB;
    }

    public void setCodeIMB(String codeIMB) {
        this.codeIMB = codeIMB;
    }

    public Date getDateCreationIMB() {
        return dateCreationIMB;
    }

    public void setDateCreationIMB(Date dateCreationIMB) {
        this.dateCreationIMB = dateCreationIMB;
    }

    public String getLibelle() {
        return libelle;
    }
    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public MissingBANBOU getMissingBANBOU() {
        return missingBANBOU;
    }
    public void setMissingBANBOU(MissingBANBOU missingBANBOU) {
        this.missingBANBOU = missingBANBOU;
    }

    public MissingAddresses(Date dateReception, Date dateLivraison, Date dateValidation, String affectation, int duree, String commentaire, String motif, String statutFacturation, Date dateDeadline, String priorite, String idTach, String commune, String codeInsee, String codePostal, String adresseFibre, String creationIPE, String codeIMB, Date dateCreationIMB,  MissingBANBOU missingBANBOU) {
        super(dateReception, dateLivraison, dateValidation, affectation, duree, commentaire, motif, statutFacturation, dateDeadline, priorite);
        this.idTach = idTach;
        this.commune = commune;
        this.codeInsee = codeInsee;
        this.codePostal = codePostal;
        this.adresseFibre = adresseFibre;
        this.creationIPE = creationIPE;
        this.codeIMB = codeIMB;
        this.dateCreationIMB = dateCreationIMB;

        this.missingBANBOU = missingBANBOU;
    }

    public MissingAddresses(String idacte, String refTacheBPU, Prestation type_prestation, String type_element, int quantite, Date dateReception, Date dateLivraison, Date dateValidation, String affectation, int duree, String commentaire, String motif, String statutFacturation, Date dateReprise, String repriseFacturable, Date dateDeadline, String priorite, Tarification tarif, String idTach, String commune, String codeInsee, String codePostal, String adresseFibre, String creationIPE, String codeIMB, Date dateCreationIMB,MissingBANBOU missingBANBOU) {
        super(idacte, refTacheBPU, type_prestation, type_element, quantite, dateReception, dateLivraison, dateValidation, affectation, duree, commentaire, motif, statutFacturation, dateReprise, repriseFacturable, dateDeadline, priorite, tarif);
        this.idTach = idTach;
        this.commune = commune;
        this.codeInsee = codeInsee;
        this.codePostal = codePostal;
        this.adresseFibre = adresseFibre;
        this.creationIPE = creationIPE;
        this.codeIMB = codeIMB;
        this.dateCreationIMB = dateCreationIMB;

        this.missingBANBOU = missingBANBOU;
    }

    public String getMotiffreafectation() {
        return motiffreafectation;
    }

    public void setMotiffreafectation(String motiffreafectation) {
        this.motiffreafectation = motiffreafectation;
    }

    public MissingAddresses() {
    }

    public MissingAddresses(Date dateReception, Date dateLivraison, Date dateValidation, String affectation, int duree, String commentaire, String motif, String statutFacturation, Date dateDeadline, String priorite, String idTach, String commune, String codeInsee, String codePostal, String adresseFibre, String creationIPE, String codeIMB, Date dateCreationIMB, String statutOperationnels,  MissingBANBOU missingBANBOU) {
        super(dateReception, dateLivraison, dateValidation, affectation, duree, commentaire, motif, statutFacturation, dateDeadline, priorite);
        this.idTach = idTach;
        this.commune = commune;
        this.codeInsee = codeInsee;
        this.codePostal = codePostal;
        this.adresseFibre = adresseFibre;
        this.creationIPE = creationIPE;
        this.codeIMB = codeIMB;
        this.dateCreationIMB = dateCreationIMB;
        this.statutOperationnels = statutOperationnels;
        this.missingBANBOU = missingBANBOU;
    }
}