package com.sofrecom.tickit.model;

import java.util.Date;

import javax.persistence.Entity;


@Entity
public class XOIP extends Acte_traitement {

	
	private String idnDI;
	private String numPrestation;
	private String etaTPrestation;
	private String typeOperation;
	private String codePrestation;
	private boolean active = true;
	
	
	public XOIP() {}

	public XOIP(String idacte, String refTacheBPU, Prestation type_prestation, String type_element, int quantite,
			Date dateReception, Date dateLivraison, Date dateValidation, String affectation, int duree,
			String commentaire, String motif, String statutFacturation, Date dateReprise, String repriseFacturable,
			Date dateDeadline, String priorite, String idnDI, String numPrestation,
			String etaTPrestation, String typeOperation,String codePrestation, boolean active) {
		super(idacte, refTacheBPU, type_prestation, type_element, quantite, dateReception, dateLivraison,
				dateValidation, affectation, duree, commentaire, motif, statutFacturation, dateReprise,
				repriseFacturable, dateDeadline, priorite);
		this.idnDI = idnDI;
		this.numPrestation = numPrestation;
		this.etaTPrestation = etaTPrestation;
		this.typeOperation = typeOperation;
		this.codePrestation = codePrestation;
		this.active = active;
	}
	

	public XOIP(String idacte, Date dateReception, String affectation, int duree, String commentaire, String idnDI,
			String numPrestation, String etaTPrestation, String typeOperation, String codePrestation, boolean active) {
		super(idacte, dateReception, affectation, duree, commentaire);
		this.idnDI = idnDI;
		this.numPrestation = numPrestation;
		this.etaTPrestation = etaTPrestation;
		this.typeOperation = typeOperation;
		this.codePrestation = codePrestation;
		this.active = active;
	}

	public XOIP(String idacte, Date dateReception, String affectation, int duree, String commentaire, String idnDI,
			String numPrestation, String etaTPrestation, String typeOperation, String codePrestation) {
		super(idacte, dateReception, affectation, duree, commentaire);
		this.idnDI = idnDI;
		this.numPrestation = numPrestation;
		this.etaTPrestation = etaTPrestation;
		this.typeOperation = typeOperation;
		this.codePrestation = codePrestation;
	}

	public String getIdnDI() {
		return idnDI;
	}

	public void setIdnDI(String idnDI) {
		this.idnDI = idnDI;
	}

	public String getNumPrestation() {
		return numPrestation;
	}

	public void setNumPrestation(String numPrestation) {
		this.numPrestation = numPrestation;
	}

	public String getEtaTPrestation() {
		return etaTPrestation;
	}

	public void setEtaTPrestation(String etaTPrestation) {
		this.etaTPrestation = etaTPrestation;
	}

	public String getTypeOperation() {
		return typeOperation;
	}

	public void setTypeOperation(String typeOperation) {
		this.typeOperation = typeOperation;
	}

	public String getCodePrestation() {
		return codePrestation;
	}

	public void setCodePrestation(String codePrestation) {
		this.codePrestation = codePrestation;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		return "XOIP [nDI=" + idnDI + ", numPrestation=" + numPrestation + ", etaTPrestation="
				+ etaTPrestation + ", typeOperation=" + typeOperation + ", active=\" + active + \"]";
	}
	
	
	

}
