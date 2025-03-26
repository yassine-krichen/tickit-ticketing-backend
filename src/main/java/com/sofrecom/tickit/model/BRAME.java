package com.sofrecom.tickit.model;

import javax.persistence.*;
import java.util.Date;

@Entity
public class BRAME extends Acte_traitement {
	private String cog;
	private String codeBanbou;
	private String codeIMB;
	private  String ui ;
	private String statutOperationnel;
	private String motifReaffectation;
	private Date dateFlux;

	private Date dateDepBan;
	private Date dateRetourBan;
	private String phase ;

	public BRAME( String cog, String motif, String type_element, String commentaire, Prestation type_prestation,int quantite, String statutFacturation, Date dateReception, Date dateDeadline, String ui ) {
		super(motif, type_element,commentaire,type_prestation, quantite, statutFacturation, dateReception, dateDeadline);
		this.cog = cog;
		this.ui = ui;
	}
//Constr de filtrage
	public BRAME(Prestation type_prestation, String type_element,Tarification tarif, int quantite, Date dateReception, Date dateLivraison, Date dateValidation, String affectation, int duree, String commentaire, String motif, String statutFacturation,Date dateDeadline,String priorite,String classification, String ui, String cog, Date dateDepBan, Date dateRetourBan, String phase, String codeBanbou, String codeIMB) {
		super(type_prestation, type_element,tarif, quantite, dateReception, dateLivraison, dateValidation, affectation, duree, commentaire, motif, statutFacturation,dateDeadline,priorite, classification);
		this.ui = ui;
		this.cog = cog;
		this.dateDepBan=dateDepBan;
		this.dateRetourBan=dateRetourBan;
		this.phase= phase;
		this.codeBanbou=codeBanbou;
		this.codeIMB=codeIMB;
	}

	public BRAME(Prestation type_prestation, String type_element, int quantite, String statutFacturation, String motif,Date dateReception,String commentaire, String cog, String ui, String phase) {
		super(type_prestation, type_element, quantite, statutFacturation,motif,dateReception,commentaire);
		this.cog = cog;
		this.ui = ui;
		this.phase= phase;
	}
	public String getCog() {
		return cog;
	}

	public void setCog(String cog) {
		this.cog = cog;
	}

	public String getCodeBanbou() {
		return codeBanbou;
	}

	public void setCodeBanbou(String codeBanbou) {
		this.codeBanbou = codeBanbou;
	}

	public String getCodeIMB() {
		return codeIMB;
	}

	public void setCodeIMB(String codeIMB) {
		this.codeIMB = codeIMB;
	}

	public String getUi() {
		return ui;
	}

	public void setUi(String ui) {
		this.ui = ui;
	}

	public String getStatutOperationnel() {
		return statutOperationnel;
	}

	public void setStatutOperationnel(String statutOperationnel) {
		this.statutOperationnel = statutOperationnel;
	}

	public Date getDateFlux() {
		return dateFlux;
	}

	public void setDateFlux(Date dateFlux) {
		this.dateFlux = dateFlux;
	}

	public String getMotifReaffectation() {
		return motifReaffectation;
	}

	public void setMotifReaffectation(String motifReaffectation) {
		this.motifReaffectation = motifReaffectation;
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

	public BRAME() {
	}


}
