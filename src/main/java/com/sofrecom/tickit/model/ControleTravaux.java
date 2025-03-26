package com.sofrecom.tickit.model;

import java.util.Date;

import javax.persistence.Entity;

@Entity
public class ControleTravaux extends Acte_traitement {

	private String ui;
	private String cog;
	private String zone;
	private String motifReaffectation;

	public ControleTravaux() {

	}

	public String getUi() {
		return ui;
	}

	public void setUi(String ui) {
		this.ui = ui;
	}

	public String getCog() {
		return cog;
	}

	public void setCog(String cog) {
		this.cog = cog;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public String getMotifReaffectation() {
		return motifReaffectation;
	}

	public void setMotifReaffectation(String motifReaffectation) {
		this.motifReaffectation = motifReaffectation;
	}

	//eya ajout, filtre
	public ControleTravaux(String idacte, Prestation prestation, String typeElement, int quantite,
						   Date dateReception, Date dateLivraison,String affectation, int duree,
						   String commentaire, String motif, String statutFacturation, String Priorite,String classification,
						   Date dateDealine, String ui, String cog,String motifReaffectation, String zone) {

		super(idacte, prestation, typeElement, quantite,dateReception,
				dateLivraison,affectation, duree,commentaire,motif, statutFacturation,
				Priorite,classification,  dateDealine);
		this.ui = ui;
		this.cog = cog;
		this.zone=zone;
		this.motifReaffectation = motifReaffectation;
	}

	//Historique
	public ControleTravaux(String idacte, Prestation prestation, String typeElement, int quantite,
						   Date dateReception, Date dateLivraison,String affectation, int duree,
						   String commentaire, String motif, String statutFacturation, String Priorite,
						   String classification,
						   Date dateDealine, String ui, String cog,String motifReaffectation) {

		super(idacte, prestation, typeElement, quantite,dateReception,
				dateLivraison,affectation, duree,commentaire,motif, statutFacturation,
				Priorite,classification, dateDealine);
		this.ui = ui;
		this.cog = cog;
		this.motifReaffectation = motifReaffectation;
	}

	//saveNewDesat eya
	public ControleTravaux(String cog, String motif, String typeElement, String commentaire,
						   Prestation pres, String statutFacturation, Date dateReception,
						   Date dateDeadline, String ui) {
		super(motif,typeElement, commentaire, pres,statutFacturation,dateReception ,dateDeadline);
		this.ui = ui;
		this.cog = cog;
	}


	public ControleTravaux(String cog, String motif, String typeElement, String commentaire,
						   Prestation pres, String statutFacturation, Date dateReception,
						   int quantite, String ui) {
		super(motif,typeElement, commentaire, pres,statutFacturation,dateReception ,quantite);
		this.ui = ui;
		this.cog = cog;
	}




}
