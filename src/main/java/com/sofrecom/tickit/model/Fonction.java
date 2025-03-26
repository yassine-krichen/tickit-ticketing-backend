package com.sofrecom.tickit.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Fonction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String id_fonction;
	private String description;
	public String getId_fonction() {
		return id_fonction;
	}
	public void setId_fonction(String id_fonction) {
		this.id_fonction = id_fonction;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Fonction(String id_fonction, String description) {
		super();
		this.id_fonction = id_fonction;
		this.description = description;
	}
	public Fonction() {
		super();
	}
	@Override
	public String toString() {
		return "Fonction [id_fonction=" + id_fonction + ", description=" + description + "]";
	}
	
	

}
