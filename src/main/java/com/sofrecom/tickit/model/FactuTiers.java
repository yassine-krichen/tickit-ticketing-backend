package com.sofrecom.tickit.model;


import javax.persistence.Entity;

@Entity
public class FactuTiers extends Acte_traitement {


    private Double mt;


    private String regionConcerne;

    private String numDossier;
    private String numOEIE;
    private String communeTravaux;

    private String etatDossier;
    private String typeDossier;
    private String postEtatdossier;

    private boolean active;

    public FactuTiers(Double mt, String numDossier, String numOEIE, String typeDossier, boolean active) {
        super ();
        this.mt = mt;
        this.numDossier = numDossier;
        this.numOEIE = numOEIE;
        this.typeDossier = typeDossier;
        this.active = active;
    }

    public FactuTiers() {
        super ();
    }

    public Double getMt() {
        return mt;
    }

    public void setMt(Double hT) {
        mt = hT;
    }

    public String getNumDossier() {
        return numDossier;
    }

    public void setNumDossier(String numDossier) {
        this.numDossier = numDossier;
    }

    public String getNumOEIE() {
        return numOEIE;
    }

    public void setNumOEIE(String numOEIE) {
        this.numOEIE = numOEIE;
    }

    public String getTypeDossier() {
        return typeDossier;
    }

    public void setTypeDossier(String typeDossier) {
        this.typeDossier = typeDossier;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "Factu_Tiers [HT=" + mt + ", numDossier=" + numDossier + ", numOEIE=" + numOEIE + ", typeDossier=" + typeDossier + "]";
    }

    public String getRegionConcerne() {
        return regionConcerne;
    }

    public void setRegionConcerne(String regionConcerne) {
        this.regionConcerne = regionConcerne;
    }


    public String getCommuneTravaux() {
        return communeTravaux;
    }

    public void setCommuneTravaux(String communeTravaux) {
        this.communeTravaux = communeTravaux;
    }

    public String getPostEtatdossier() {
        return postEtatdossier;
    }

    public void setPostEtatdossier(String postEtatdossier) {
        this.postEtatdossier = postEtatdossier;
    }

	public String getEtatDossier() {
		return etatDossier;
	}

	public void setEtatDossier(String etatDossier) {
		this.etatDossier = etatDossier;
	}
}
