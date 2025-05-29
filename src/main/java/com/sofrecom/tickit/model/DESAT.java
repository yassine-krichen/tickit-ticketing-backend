package com.sofrecom.tickit.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class DESAT  extends Acte_traitement{
	private String zone;
	private String fi;
	private int nbreCoupleurs;
	private String motifReaffectation;
	private String statut_operatonnels;
	private Date date_reafictation;
	private String cog;
	private String ui;
	private Date date_liv_reafictation;

	

	public String getZone() {
		return zone;
	}
	public void setZone(String zone) {
		this.zone = zone;
	}
	public String getFi() {
		return fi;
	}
	public void setFi(String fi) {
		this.fi = fi;
	}
	public String getCog() {
		return cog;
	}
	public void setCog(String cog) {
		this.cog = cog;
	}
	
	public String getMotifReaffectation() {
		return motifReaffectation;
	}
	public void setMotifReaffectation(String motifReaffectation) {
		this.motifReaffectation = motifReaffectation;
	}
	public String getStatut_operatonnels() {
		return statut_operatonnels;
	}
	public void setStatut_operatonnels(String statut_operatonnels) {
		this.statut_operatonnels = statut_operatonnels;
	}
	
	
	public String getUi() {
		return ui;
	}
	public void setUi(String ui) {
		this.ui = ui;
	}
	
	
	public Date getDate_reafictation() {
		return date_reafictation;
	}
	public void setDate_reafictation(Date date_reafictation) {
		this.date_reafictation = date_reafictation;
	}
	public Date getDate_liv_reafictation() {
		return date_liv_reafictation;
	}
	public void setDate_liv_reafictation(Date date_liv_reafictation) {
		this.date_liv_reafictation = date_liv_reafictation;
	}

	
	
	public int getNbreCoupleurs() {
		return nbreCoupleurs;
	}
	public void setNbreCoupleurs(int nbreCoupleurs) {
		this.nbreCoupleurs = nbreCoupleurs;
	}
	// affichage 
	@Override
	public String toString() {
		return "DESAT [ ZONE=" + zone + ", FI=" + fi + ", date_refus=" 
				+ ", motifReaffectation=" + motifReaffectation + ", statut_operatonnels=" + statut_operatonnels + ", date_reafictation="
				+ date_reafictation + ", COG=" + cog+ ", ui=" + ui + ", date_liv_reafictation=" + date_liv_reafictation
				+ "]";
	}

	
	 public DESAT() {
	    }
	
public DESAT(String cog,String fi,String motif, String type_element, String commentaire, Prestation type_prestation, float quantite,
		String statutFacturation, Date dateReception, Date dateDeadline,String ui) {
	super(motif, type_element, commentaire, type_prestation, quantite, statutFacturation, dateReception, dateDeadline);
	// TODO Auto-generated constructor stub
	this.cog= cog;
	this.fi= fi;
	this.ui = ui;
	
}

public DESAT(String zONE,String fI, String motifReaffectation,String statut_operatonnels,
		Date date_reafictation,String cOG, String ui, Date date_liv_reafictation, Prestation type_prestation, String type_element,
		Date dateReception, float quantite,
		Date dateLivraison, Date dateValidation, String affectation, String commentaire,int duree,
		String refTacheBPU, String motif,Date dateDeadline, String priorite, String statutFacturation, int nbreCoupleurs ) {
	super(refTacheBPU, type_prestation, type_element, quantite, dateReception, dateLivraison, dateValidation,
			affectation, duree, commentaire, motif,dateDeadline,priorite,statutFacturation);
	
	zone = zONE;
	fi = fI;
	
	this.motifReaffectation = motifReaffectation;
	this.statut_operatonnels = statut_operatonnels;
	this.date_reafictation = date_reafictation;
	cog = cOG;
	this.ui = ui;
	this.date_liv_reafictation = date_liv_reafictation;
	this.nbreCoupleurs= nbreCoupleurs;
}



// constructeur ajout dessat 
public DESAT(String idacte, String refTacheBPU, Prestation type_prestation, String type_element, float quantite, Date dateReception,
		Date dateLivraison, Date dateValidation, String affectation, int duree, String commentaire, String motif,
		String statutFacturation, Date dateReprise, String repriseFacturable, Date dateDeadline, String priorite, String classification,
			 String zONE,String fI, String motifReaffectation,String statut_operatonnels,
		Date date_reafictation,String cOG, String ui, Date date_liv_reafictation, int nbreCoupleurs) {
	super(idacte, refTacheBPU, type_prestation, type_element, quantite, dateReception, dateLivraison, dateValidation, affectation,
			duree, commentaire, motif, statutFacturation, dateReprise, repriseFacturable, dateDeadline, priorite, classification);
	// TODO Auto-generated constructor stub
	
	this.zone = zONE;
	this.fi = fI;
	this.motifReaffectation = motifReaffectation;
	this.statut_operatonnels = statut_operatonnels;
	this.date_reafictation = date_reafictation;
	this.cog = cOG;
	this.ui = ui;
	this.date_liv_reafictation = date_liv_reafictation;
	this.nbreCoupleurs = nbreCoupleurs;
}

	// constructeur reaff dessat
	public DESAT( Prestation type_prestation, String type_element, float quantite, Date dateReception,
				 Date dateLivraison, Date dateValidation, String affectation, int duree, String commentaire, String motif,
				 String statutFacturation, Date dateReprise, String repriseFacturable, Date dateDeadline, String priorite, String zONE,String fI,
				 String motifReaffectation,String statut_operatonnels,
				 Date date_reafictation,String cOG, String ui, Date date_liv_reafictation, int nbreCoupleurs) {
		super( type_prestation, type_element, quantite, dateReception, dateLivraison, dateValidation, affectation,
				duree, commentaire, motif, statutFacturation, dateReprise, repriseFacturable, dateDeadline, priorite);
		// TODO Auto-generated constructor stub

		this.zone = zONE;
		this.fi = fI;
		this.motifReaffectation = motifReaffectation;
		this.statut_operatonnels = statut_operatonnels;
		this.date_reafictation = date_reafictation;
		this.cog = cOG;
		this.ui = ui;
		this.date_liv_reafictation = date_liv_reafictation;
		this.nbreCoupleurs = nbreCoupleurs;
	}

//Filtrer Desat
	public DESAT( Prestation type_prestation, String type_element, float quantite, Date dateReception,
				 Date dateLivraison, String affectation, int duree, String commentaire, String motif,
				 String statutFacturation, Date dateDeadline, String priorite, String zONE,String fI,
				 String motifReaffectation,
				String cOG, String ui, int nbreCoupleurs) {
		super(type_prestation, type_element, quantite, dateReception, dateLivraison, affectation,
				duree, commentaire, motif, statutFacturation, dateDeadline, priorite);
		this.zone = zONE;
		this.fi = fI;
		this.motifReaffectation = motifReaffectation;
		this.cog = cOG;
		this.ui = ui;
		this.nbreCoupleurs = nbreCoupleurs;
	}
}
