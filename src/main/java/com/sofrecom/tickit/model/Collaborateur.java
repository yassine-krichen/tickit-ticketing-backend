package com.sofrecom.tickit.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.*;

import javax.persistence.*;

@Entity
public class Collaborateur   {
	@Id
	private String CUID;
	private String nom;
	private String prenom;
	private String adresse;
	private String mdp;
	private Date dateInscription;
	private String fonction;

	@ManyToOne
	@JoinColumn(name="id_equipe")
	Equipe equipe;

	private String photo;

	@Column ( nullable = true )
	private int telephone;

	@Column ( nullable = true )
	private boolean active=false;

	@Column ( nullable = true )
	private String email;


	@ManyToMany(mappedBy = "collaborateurs", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	@JsonIgnore
	private List<SousEquipe> sousEquipes = new ArrayList<>();

	public List<SousEquipe> getSousEquipes() {
		return sousEquipes;
	}

	public void setSousEquipes(List<SousEquipe> sousEquipes) {
		this.sousEquipes = sousEquipes;
	}

	public String getCUID() {
		return CUID;
	}
	public void setCUID(String cUID) {
		CUID = cUID;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getPrenom() {
		return prenom;
	}
	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}
	public String getAdresse() {
		return adresse;
	}
	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}
	public String getMdp() {
		return mdp;
	}
	public void setMdp(String mdp) {
		this.mdp = mdp;
	}
	public Date getDateInscription() {
		return dateInscription;
	}
	public void setDateInscription(Date dateInscription) {
		this.dateInscription = dateInscription;
	}
	public String getFonction() {
		return fonction;
	}
	public void setFonction(String fonction) {
		this.fonction = fonction;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public int getTelephone() {
		return telephone;
	}

	public void setTelephone(int telephone) {
		this.telephone = telephone;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Collaborateur() {
		super();
	}
	public Collaborateur(String cUID, String nom, String prenom, String adresse, String mdp, Date dateInscription,
			 String fonction) {
		super();
		CUID = cUID;
		this.nom = nom;
		this.prenom = prenom;
		this.adresse = adresse;
		this.mdp = mdp;
		this.dateInscription = dateInscription;
		this.fonction = fonction;
	}

	public Equipe getEquipe() {
		return equipe;
	}

	public void setEquipe(Equipe equipe) {
		this.equipe = equipe;
	}

	public Collaborateur(String cUID, String nom, String prenom, Equipe equipe,Date dateInscription,int telephone,String email) {
		super();
		CUID = cUID;
		this.nom = nom;
		this.prenom = prenom;
		this.equipe = equipe;
		this.dateInscription=dateInscription;
		this.telephone=telephone;
		this.email=email;

	}

	public Collaborateur(String cUID, String nom, String prenom, String adresse, String mdp, Date dateInscription,
			String fonction, Equipe equipe) {
		super();
		CUID = cUID;
		this.nom = nom;
		this.prenom = prenom;
		this.adresse = adresse;
		this.mdp = mdp;
		this.dateInscription = dateInscription;
		this.fonction = fonction;
		this.equipe = equipe;
	}

	public Collaborateur(String CUID, String nom, String prenom, String adresse, String mdp, Date dateInscription, String fonction, Equipe equipe, String photo, int telephone, boolean active) {
		this.CUID = CUID;
		this.nom = nom;
		this.prenom = prenom;
		this.adresse = adresse;
		this.mdp = mdp;
		this.dateInscription = dateInscription;
		this.fonction = fonction;
		this.equipe = equipe;
		this.photo = photo;
		this.telephone = telephone;
		this.active = active;
	}
	public Collaborateur(String cUID, String nom, String prenom, Date dateInscription, Equipe equipe, int telephone,
			boolean active, String email) {
		super();
		CUID = cUID;
		this.nom = nom;
		this.prenom = prenom;
		this.dateInscription = dateInscription;
		this.equipe = equipe;
		this.telephone = telephone;
		this.active = active;
		this.email = email;
	}
	
	
}