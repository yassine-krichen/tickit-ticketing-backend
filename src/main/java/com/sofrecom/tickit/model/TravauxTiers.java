package com.sofrecom.tickit.model;

import java.util.Date;

import javax.persistence.Entity;

@Entity
public class TravauxTiers extends Acte_traitement {
	

	private String idnD;
	private String oT; 
	private String action;
	private String motifTravTiers;
	private String prestationTravTiers;
	private boolean active =true;


	public TravauxTiers(Prestation typeprestation, String type_element, int quantite, Date dateReception, Date dateLivraison, String affectation, int duree, String commentaire, String motif, String statutFacturation, Date dateDeadline, String priorite, String idnD, String oT, String action, String motifTravTiers, String prestationTravTiers, boolean active) {
		super ( typeprestation, type_element, quantite, dateReception, dateLivraison, affectation, duree, commentaire, motif, statutFacturation, dateDeadline, priorite );
		this.idnD = idnD;
		this.oT = oT;
		this.action = action;
		this.motifTravTiers = motifTravTiers;
		this.prestationTravTiers = prestationTravTiers;
		this.active = active;
	}


	public TravauxTiers() {}

	public TravauxTiers(Object o, String forfait, int i, Date dateLivraison, Date dateLivraison1, String affectation, int duree, String commentaire, String s, String toString, String p1, Object o1, Object o2, String getnD, String getoT, String action, String motifTravTiers, String prestationTravTiers, boolean active) {
	}

	public String getnD() {
		return idnD;
	}
	public void setnD(String nD) {
		this.idnD = nD;
	}
	public String getoT() {
		return oT;
	}
	public void setoT(String oT) {
		this.oT = oT;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}


	public String getMotifTravTiers() {
		return motifTravTiers;
	}



	public void setMotifTravTiers(String motif) {
		this.motifTravTiers = motif;
	}



	public String getPrestationTravTiers() {
		return prestationTravTiers;
	}



	public void setPrestationTravTiers(String prestationTravTiers) {
		this.prestationTravTiers = prestationTravTiers;
	}


	public String getIdnD() {
		return idnD;
	}

	public void setIdnD(String idnD) {
		this.idnD = idnD;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}


}
