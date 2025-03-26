package com.sofrecom.tickit.model;

import javax.persistence.*;


@Entity
	public class Equipe {
		@Id
		private int idEquipe;
		private String nom;


	public int getIdEquipe() {
		return idEquipe;
	}
	public void setIdEquipe(int idEquipe) {
		this.idEquipe = idEquipe;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public Equipe() {
		super();
	}
	public Equipe(int idEquipe, String nom) {
		super();
		this.idEquipe = idEquipe;
		this.nom = nom;
	}
	@Override
	public String toString() {
		return "Equipe [id_equipe=" + idEquipe + ", nom=" + nom + "]";
	}


}