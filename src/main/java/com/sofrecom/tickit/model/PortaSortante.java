package com.sofrecom.tickit.model;

import java.util.Date;

import javax.persistence.Entity;

@Entity
public class PortaSortante extends Acte_traitement {
	
	private String idnDI;
	private String nature_ndi;
	private String type_operation;
	private String nb_SDAs;
	private boolean active =  true;
	
	public String getIdnDI() {
		return idnDI;
	}
	public void setIdnDI(String idnDI) {
		this.idnDI = idnDI;
	}
	
	public String getNature_ndi() {
		return nature_ndi;
	}
	public void setNature_ndi(String nature_ndi) {
		this.nature_ndi = nature_ndi;
	}
	
	public String getType_operation() {
		return type_operation;
	}
	public void setType_operation(String type_operation) {
		this.type_operation = type_operation;
	}
	
	public String getNb_SDAs() {
		return nb_SDAs;
	}
	public void setNb_SDAs(String nb_SDAs) {
		this.nb_SDAs = nb_SDAs;
	}	
	
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public PortaSortante() {
		super();
	}

	@Override
	public String toString() {
		return "PortaSortante [idnDI=" + idnDI + ", nature_ndi=" + nature_ndi + ", type_operation=" + type_operation
				+ ", nb_SDAs=" + nb_SDAs + "]";
	}

	public PortaSortante(Prestation typeprestation, String type_element, int quantite, Date dateReception, Date dateLivraison, String affectation, int duree, String commentaire, String motif, String statutFacturation, String priorite, Tarification tarif, Date dateFacturation, String idnDI, String nature_ndi, String type_operation, String nb_SDAs, boolean active) {
		super ( typeprestation, type_element, quantite, dateReception, dateLivraison, affectation, duree, commentaire, motif, statutFacturation, priorite, tarif, dateFacturation );
		this.idnDI = idnDI;
		this.nature_ndi = nature_ndi;
		this.type_operation = type_operation;
		this.nb_SDAs = nb_SDAs;
		this.active = active;
	}
}
