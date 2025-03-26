package com.sofrecom.tickit.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "missing_banbou")
public class MissingBANBOU  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @OneToMany(mappedBy = "missingBANBOU", cascade = CascadeType.ALL,fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private List<MissingAddresses> missingAddresseses = new ArrayList<> ();

    @Column(name = "tache")
    private String tache;

    @Column(name = "UCI")
    private String uic;

    @Column(name = "idTach")
    private String idTach;

    @Column(name = "commune")
    private String commune;

    @Column(name = "codeInsee")
    private String codeInsee;

    @Column(name = "typeElement")
    private String typeElement;

    @Column(name = "statut")
    private String statut;
    @Column(name = "NbreAdr")
    private int nbrAdr;

    @Column(name = "dateAttribution")
    @Temporal(TemporalType.DATE)
    private Date dateAttribution;

    @Column(name = "dateAffectation")
    @Temporal(TemporalType.DATE)
    private Date dateAffectation;

    @Column(name = "dateLivraison")
    @Temporal(TemporalType.DATE)
    private Date dateLivraison;



    @Column(name = "duree")
    private Integer duree;

    @Column(name = "affectation")
    private String affectation;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "idPrestation")
    Prestation typeprestation;

    @Column(name = "active")
    private boolean active;

    public void addMissingAddress(MissingAddresses address) {
        missingAddresseses.add(address);
        address.setMissingBANBOU(this);  // Ensure bidirectional relationship
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Prestation getTypeprestation() {
        return typeprestation;
    }

    public void setTypeprestation(Prestation typeprestation) {
        this.typeprestation = typeprestation;
    }

    public List<MissingAddresses> getMissingAddresseses() {
        return missingAddresseses;
    }

    public void setMissingAddresseses(List<MissingAddresses> missingAddresseses) {
        this.missingAddresseses = missingAddresseses;
    }

    public String getTache() {
        return tache;
    }

    public void setTache(String tache) {
        this.tache = tache;
    }

    public String getUci() {
        return uic;
    }

    public void setUci(String uci) {
        this.uic = uci;
    }

    public String getIdTach() {
        return idTach;
    }

    public void setIdTach(String idTach) {
        this.idTach = idTach;
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

    public String getTypeElement() {
        return typeElement;
    }

    public String getUic() {
        return uic;
    }

    public void setUic(String uic) {
        this.uic = uic;
    }

    public int getNbreAdr() {
        return nbrAdr;
    }

    public void setNbreAdr(int nbreAdr) {
        nbrAdr = nbreAdr;
    }

    public void setTypeElement(String typeElement) {
        this.typeElement = typeElement;
    }

    public Date getDateAttribution() {
        return dateAttribution;
    }

    public void setDateAttribution(Date dateAttribution) {
        this.dateAttribution = dateAttribution;
    }

    public Date getDateAffectation() {
        return dateAffectation;
    }

    public void setDateAffectation(Date dateAffectation) {
        this.dateAffectation = dateAffectation;
    }

    public Date getDateLivraison() {
        return dateLivraison;
    }

    public void setDateLivraison(Date dateLivraison) {
        this.dateLivraison = dateLivraison;
    }



    public Integer getDuree() {
        return duree;
    }

    public void setDuree(Integer duree) {
        this.duree = duree;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getAffectation() {
        return affectation;
    }

    public void setAffectation(String affectation) {
        this.affectation = affectation;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public MissingBANBOU() {
    }

    public MissingBANBOU(long id, String tache, String uci, String idTach, String commune, String codeInsee, String typeElement, Integer nbreAdr, Date dateAttribution, Date dateAffectation, Date dateLivraison, Integer duree, String affectation, boolean active) {
        this.id = id;
        this.tache = tache;
        this.uic = uci;
        this.idTach = idTach;
        this.commune = commune;
        this.codeInsee = codeInsee;
        this.typeElement = typeElement;
        this.nbrAdr = nbreAdr;
        this.dateAttribution = dateAttribution;
        this.dateAffectation = dateAffectation;
        this.dateLivraison = dateLivraison;
        this.duree = duree;
        this.affectation = affectation;
        this.active = active;
    }

    public MissingBANBOU(long id, List<MissingAddresses> missingAddresseses, String tache, String uci, String idTach, String commune, String codeInsee, String typeElement, Integer nbreAdr, Date dateAttribution, Date dateAffectation, Date dateLivraison, Integer duree, String affectation, Prestation typeprestation, boolean active) {
        this.id = id;
        this.missingAddresseses = missingAddresseses;
        this.tache = tache;
        this.uic = uci;
        this.idTach = idTach;
        this.commune = commune;
        this.codeInsee = codeInsee;
        this.typeElement = typeElement;
        this.nbrAdr = nbreAdr;
        this.dateAttribution = dateAttribution;
        this.dateAffectation = dateAffectation;
        this.dateLivraison = dateLivraison;
        this.duree = duree;
        this.affectation = affectation;
        this.typeprestation = typeprestation;
        this.active = active;
    }

    public MissingBANBOU(long id, List<MissingAddresses> missingAddresseses, String tache, String uci, String idTach, String commune, String codeInsee, String typeElement, String staut, Integer nbreAdr, Date dateAttribution, Date dateAffectation, Date dateLivraison, Integer duree, String affectation, Prestation typeprestation, boolean active) {
        this.id = id;
        this.missingAddresseses = missingAddresseses;
        this.tache = tache;
        this.uic = uci;
        this.idTach = idTach;
        this.commune = commune;
        this.codeInsee = codeInsee;
        this.typeElement = typeElement;
        this.statut = staut;
        this.nbrAdr = nbreAdr;
        this.dateAttribution = dateAttribution;
        this.dateAffectation = dateAffectation;
        this.dateLivraison = dateLivraison;
        this.duree = duree;
        this.affectation = affectation;
        this.typeprestation = typeprestation;
        this.active = active;
    }


}