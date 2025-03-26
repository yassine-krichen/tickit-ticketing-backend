package com.sofrecom.tickit.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;

@Entity
public class GeoReseau extends Acte_traitement{

    private String idTache;
    private String zone;
    private String commune;
    private String codeInsee;
    private String statut;
    private String siOrange;
    private String type;
    private String source;
    @Column(columnDefinition = "TEXT")
    @Basic(fetch = FetchType.LAZY)
    private String listeSiteSupport;
    private int nbrSiteSupport;
    @Column(columnDefinition = "TEXT")
    @Basic(fetch = FetchType.LAZY)
    private String listeIMB;
    private int nbrIMB;
    @Column(columnDefinition = "TEXT")
    @Basic(fetch = FetchType.LAZY)
    private String listeCable;
    private int nbrCable;

    public GeoReseau() {
    }

    public GeoReseau(GeoReseau geo) {
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

    public String getCodeInsee() {
        return codeInsee;
    }

    public void setCodeInsee(String codeInsee) {
        this.codeInsee = codeInsee;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getSiOrange() {
        return siOrange;
    }

    public void setSiOrange(String siOrange) {
        this.siOrange = siOrange;
    }

    public String getListeSiteSupport() {
        return listeSiteSupport;
    }

    public void setListeSiteSupport(String listeSiteSupport) {
        this.listeSiteSupport = listeSiteSupport;
    }

    public int getNbrSiteSupport() {
        return nbrSiteSupport;
    }

    public void setNbrSiteSupport(int nbrSiteSupport) {
        this.nbrSiteSupport = nbrSiteSupport;
    }

    public String getListeIMB() {
        return listeIMB;
    }

    public void setListeIMB(String listeIMB) {
        this.listeIMB = listeIMB;
    }

    public int getNbrIMB() {
        return nbrIMB;
    }

    public void setNbrIMB(int nbrIMB) {
        this.nbrIMB = nbrIMB;
    }

    public String getListeCable() {
        return listeCable;
    }

    public void setListeCable(String listeCable) {
        this.listeCable = listeCable;
    }

    public int getNbrCable() {
        return nbrCable;
    }

    public void setNbrCable(int nbrCable) {
        this.nbrCable = nbrCable;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
