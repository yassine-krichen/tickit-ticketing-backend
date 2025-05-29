package com.sofrecom.tickit.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class Prestation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idPrestation;
	private String nomPrestation;
	private int deadline;// délaiTraitement
	

	
	public int getIdPrestation() {
		return idPrestation;
	}
	public void setIdPrestation(int idPrestation) {
		this.idPrestation = idPrestation;
	}
	public String getNomPrestation() {
		return nomPrestation;
	}
	public void setNomPrestation(String nomPrestation) {
		this.nomPrestation = nomPrestation;
	}
	public int getDeadline() {
		return deadline;
	}
	public void setDeadline(int deadline) {
		this.deadline = deadline;
	}
	
	

	
	@Override
	public String toString() {
		return "Prestation [idPrestation=" + idPrestation + ", nomPrestation=" + nomPrestation + ", deadline="
				+ deadline + ", actesTraitement=" +  "]";
	}
	public Prestation() {
		super();
	}
	
	
	public Prestation(String nomPrestation, int deadline) {
		super();
		this.nomPrestation = nomPrestation;
		this.deadline = deadline;
	}

	public Prestation(Integer idPrestation, String nomPrestation, int deadline) {
		super();
		this.idPrestation = idPrestation;
		this.nomPrestation = nomPrestation;
		this.deadline = deadline;
	}
	
	
	
	
	
	
	
	
	
	

}
