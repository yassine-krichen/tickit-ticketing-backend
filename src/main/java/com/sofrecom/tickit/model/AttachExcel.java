package com.sofrecom.tickit.model;

public class AttachExcel {
	private String tache;
	private String refDossier;
	private String unite;
	private String quantite;
	private String bpu;
	private String prixTotal;
	
	
	
	public AttachExcel() {
		super();
	}
	public AttachExcel(String tache, String refDossier, String unite, String quantite, String bpu, String prixTotal) {
		super();
		this.tache = tache;
		this.refDossier = refDossier;
		this.unite = unite;
		this.quantite = quantite;
		this.bpu = bpu;
		this.prixTotal = prixTotal;
	}
	public String getTache() {
		return tache;
	}
	public void setTache(String tache) {
		this.tache = tache;
	}
	public String getRefDossier() {
		return refDossier;
	}
	public void setRefDossier(String refDossier) {
		this.refDossier = refDossier;
	}
	public String getUnite() {
		return unite;
	}
	public void setUnite(String unite) {
		this.unite = unite;
	}
	public String getQuantite() {
		return quantite;
	}
	public void setQuantite(String quantite) {
		this.quantite = quantite;
	}
	public String getBpu() {
		return bpu;
	}
	public void setBpu(String bpu) {
		this.bpu = bpu;
	}
	public String getPrixTotal() {
		return prixTotal;
	}
	public void setPrixTotal(String prixTotal) {
		this.prixTotal = prixTotal;
	}
	
}
