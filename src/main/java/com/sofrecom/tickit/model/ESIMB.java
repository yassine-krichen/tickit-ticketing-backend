package com.sofrecom.tickit.model;


import java.util.Date;

import javax.persistence.Entity;


@Entity
public class ESIMB extends Acte_traitement {
	
	
	private String codeBanbou;
	private String codeIMB;
	private Date dateVerification;
	
	
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
	
	public Date getDateVerification() {
		return dateVerification;
	}
	public void setDateVerification(Date dateVerification) {
		this.dateVerification = dateVerification;
	}
	public ESIMB(String codeIMB, Date dateVerification) {
		super();
		this.codeIMB = codeIMB;
		this.dateVerification = dateVerification;
	}
	public ESIMB() {
		super();
	}
	
	
	
	public ESIMB(String idacte, String refTacheBPU, Prestation type_prestation, String type_element, int quantite,
			Date dateReception, Date dateLivraison, Date dateValidation,String affectation, int duree,
			String priorite,  String commentaire, String motif, Date dateDeadline,
			 String statutFacturation,String repriseFacturable,String classification, String codeBanbou, Date dateReprise,
			 Date dateVerification, String codeIMB, Tarification tarif) {
		super(idacte, refTacheBPU, type_prestation, type_element, quantite, dateReception, dateLivraison,
				dateValidation, affectation, dateDeadline, priorite, duree, commentaire, motif, statutFacturation,
				dateReprise, repriseFacturable, classification);
		this.codeBanbou = codeBanbou;
		this.codeIMB = codeIMB;
		this.dateVerification = dateVerification;
	}
	
	public ESIMB(String idacte, String refTacheBPU, Prestation type_prestation, String type_element, int quantite,
			Date dateReception, Date dateLivraison, Date dateValidation,String affectation, int duree,
			String priorite,  String commentaire, String motif, Date dateDeadline,
			 String statutFacturation,String repriseFacturable,String classification, String codeBanbou, Date dateReprise,
			 Date dateVerification, String codeIMB) {
		super(idacte, refTacheBPU, type_prestation, type_element, quantite, dateReception, dateLivraison,
				dateValidation, affectation, dateDeadline, priorite, duree, commentaire, motif, statutFacturation,
				dateReprise, repriseFacturable, classification);
		this.codeBanbou = codeBanbou;
		this.codeIMB = codeIMB;
		this.dateVerification = dateVerification;
	}
	public ESIMB(String idacte, String refTacheBPU, Prestation type_prestation, String type_element, int quantite,
			Date dateReception, Date dateLivraison, Date dateValidation, String affectation, int duree,
			String commentaire, String motif, String statutFacturation, Date dateReprise, String repriseFacturable,
			String codeBanbou, String codeIMB, Date dateVerification, boolean active) {
		super(idacte, refTacheBPU, type_prestation, type_element, quantite, dateReception, dateLivraison,
				dateValidation, affectation, duree, commentaire, motif, statutFacturation, dateReprise,
				repriseFacturable);
		this.codeBanbou = codeBanbou;
		this.codeIMB = codeIMB;
		this.dateVerification = dateVerification;
	}
	
	
	
	public ESIMB(String idacte, String refTacheBPU, Prestation type_prestation, String type_element, int quantite, Date dateReception,
			Date dateLivraison, Date dateValidation, String affectation, int duree, String commentaire,
			String motif, String statutFacturation,Date dateReprise, String repriseFacturable, 
			String codeIMB, Date dateVerification,String codeBanbou) {
		super(refTacheBPU, type_prestation, type_element, quantite, dateReception, dateLivraison, dateValidation,
				affectation, duree, commentaire, motif, statutFacturation, dateReprise, repriseFacturable);
		this.codeBanbou = codeBanbou;
		this.codeIMB = codeIMB;
		this.dateVerification = dateVerification;
	}

	@Override
	public String toString() {
		return "ESIMB [codeBanbou=" + codeBanbou + ", codeIMB=" + codeIMB + ", dateVerification=" + dateVerification
				+ "]";
	}

	
	
	
	
}
