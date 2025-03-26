package com.sofrecom.tickit.model;

import java.util.Date;


public class Attachements {




	private String idacte;
	private Prestation typeprestation;
	private String RefDossier;

	private int quantite;
	private String tarif;
	private int Duree;
	private float prix_total;
	private BPU bpu;
	private String type_element ;

	private boolean error;
	private String errorMessage;

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	private Date DateFacturation ;

private Date DateLivraison ;

	public Date getDateLivraison() {
	return DateLivraison;
}

public void setDateLivraison(Date dateLivraison) {
	DateLivraison = dateLivraison;
}

	public String getIdacte() {
		return idacte;
	}

	public void setIdacte(String idacte) {
		this.idacte = idacte;
	}

	public Prestation getTypeprestation() {
		return typeprestation;
	}

	public Date getDateFacturation() {
		return DateFacturation;
	}

	public void setDateFacturation(Date dateFacturation) {
		DateFacturation = dateFacturation;
	}

	public void setTypeprestation(Prestation type_prestation) {
		this.typeprestation = type_prestation;
	}

	public String getRefDossier() {
		return RefDossier;
	}

	public void setRefDossier(String refDossier) {
		RefDossier = refDossier;
	}

	public int getQuantite() {
		return quantite;
	}

	public void setQuantite(int quantite) {
		this.quantite = quantite;
	}

	public String getTarif() {
		return tarif;
	}

	public void setTarif(String tarif) {
		this.tarif = tarif;
	}

	public int getDuree() {
		return Duree;
	}

	public void setDuree(int duree) {
		Duree = duree;
	}

	public float getPrix_total() {
		return prix_total;
	}

	public void setPrix_total(float prix_total) {
		this.prix_total = prix_total;
	}

	public BPU getBpu() {
		return bpu;
	}

	public void setBpu(BPU bpu) {
		this.bpu = bpu;
	}

	public String getType_element() {
		return type_element;
	}

	public void setType_element(String type_element) {
		this.type_element = type_element;
	}

	public Attachements() {
	}

	public Attachements(String idacte, Prestation type_prestation, String refDossier, int quantite, String tarif, int duree, float prix_total, BPU bpu) {
		this.idacte = idacte;
		this.typeprestation = type_prestation;
		RefDossier = refDossier;
		this.quantite = quantite;
		this.tarif = tarif;
		Duree = duree;
		this.prix_total = prix_total;
		this.bpu = bpu;
	}

	public Attachements(String idacte, Prestation typeprestation, String refDossier, int quantite, String tarif,
			int duree, float prix_total, BPU bpu, String type_element) {
		super();
		this.idacte = idacte;
		this.typeprestation = typeprestation;
		RefDossier = refDossier;
		this.quantite = quantite;
		this.tarif = tarif;
		Duree = duree;
		this.prix_total = prix_total;
		this.bpu = bpu;
		this.type_element = type_element;
	}

	public Attachements(String idacte, Prestation typeprestation, String refDossier, int quantite, String tarif,
			int duree, float prix_total, BPU bpu, String type_element, Date dateFacturation) {
		super();
		this.idacte = idacte;
		this.typeprestation = typeprestation;
		RefDossier = refDossier;
		this.quantite = quantite;
		this.tarif = tarif;
		Duree = duree;
		this.prix_total = prix_total;
		this.bpu = bpu;
		this.type_element = type_element;
		DateFacturation = dateFacturation;
	}

	public Attachements(String idacte, Prestation typeprestation, String refDossier, int quantite, String tarif,
			int duree, float prix_total, BPU bpu, String type_element, Date dateFacturation, Date dateLivraison) {
		super();
		this.idacte = idacte;
		this.typeprestation = typeprestation;
		RefDossier = refDossier;
		this.quantite = quantite;
		this.tarif = tarif;
		Duree = duree;
		this.prix_total = prix_total;
		this.bpu = bpu;
		this.type_element = type_element;
		DateFacturation = dateFacturation;
		DateLivraison = dateLivraison;
	}

	@Override
	public String toString() {
		return "Attachements{" +
				"idacte='" + idacte + '\'' +
				", typeprestation=" + typeprestation +
				", RefDossier='" + RefDossier + '\'' +
				", quantite=" + quantite +
				", tarif='" + tarif + '\'' +
				", Duree=" + Duree +
				", prix_total=" + prix_total +
				", bpu=" + bpu +
				", type_element='" + type_element + '\'' +
				", error=" + error +
				", errorMessage='" + errorMessage + '\'' +
				", DateFacturation=" + DateFacturation +
				", DateLivraison=" + DateLivraison +
				'}';
	}
}
