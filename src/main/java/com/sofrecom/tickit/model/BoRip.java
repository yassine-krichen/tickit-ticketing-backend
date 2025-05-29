package com.sofrecom.tickit.model;

import javax.persistence.Entity;
import javax.validation.constraints.Pattern;

@Entity
public class BoRip extends Acte_traitement{
    private String idTache;
    private String poi;
    @Pattern(regexp = "^[a-zA-Z0-9àâäéèêëîïôöùûüÿç+.,\\s-]+$", message = "Caractères non autorisés")
    private String type;
    private String etat;
    private String doe;
    private String pushMail;
    private int nbVersion;
    private int numSemaine;
    private int delai;
    private String jalon;

    public BoRip() {
    }

    public BoRip(BoRip bio) {
        super();
    }

    public String getIdTache() {
        return idTache;
    }

    public void setIdTache(String idTache) {
        this.idTache = idTache;
    }

    public String getPoi() {
        return poi;
    }

    public void setPoi(String poi) {
        this.poi = poi;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public String getDoe() {
        return doe;
    }

    public void setDoe(String doe) {
        this.doe = doe;
    }

    public String getPushMail() {
        return pushMail;
    }

    public void setPushMail(String pushMail) {
        this.pushMail = pushMail;
    }

    public int getNbVersion() {
        return nbVersion;
    }

    public void setNbVersion(int nbVersion) {
        this.nbVersion = nbVersion;
    }

    public int getNumSemaine() {
        return numSemaine;
    }

    public void setNumSemaine(int numSemaine) {
        this.numSemaine = numSemaine;
    }

    public String getJalon() {
        return jalon;
    }

    public void setJalon(String jalon) {
        this.jalon = jalon;
    }

    public int getDelai() {
        return delai;
    }

    public void setDelai(int delai) {
        this.delai = delai;
    }
}
