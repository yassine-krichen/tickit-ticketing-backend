package com.sofrecom.tickit.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Tarification {
	@Id
	private String codeTarif;
	@ManyToOne
	@JoinColumn(name = "idPrestation")
	Prestation typeprestation;
	private long valeur;


	public Tarification(String codeTarif, Prestation typeprestation) {
		this.codeTarif = codeTarif;
		this.typeprestation = typeprestation;
	}

	public String getCodeTarif() {
		return codeTarif;
	}
	public void setCodeTarif(String codeTarif) {
		this.codeTarif = codeTarif;
	}



	public Prestation getTypeprestation() {
		return typeprestation;
	}
	public void setTypeprestation(Prestation typeprestation) {
		this.typeprestation = typeprestation;
	}
	public long getValeur() {
		return valeur;
	}
	public void setValeur(long valeur) {
		this.valeur = valeur;
	}
	public Tarification() {
		super();
	}
	public Tarification(String codeTarif, Prestation typeprestation, long valeur) {
		super();
		this.codeTarif = codeTarif;
		this.typeprestation = typeprestation;
		this.valeur = valeur;
	}
	@Override
	public String toString() {
		return "Tarification [codeTarif=" + codeTarif + ", typeprestation=" + typeprestation + ", valeur=" + valeur
				+ "]";
	}















}
