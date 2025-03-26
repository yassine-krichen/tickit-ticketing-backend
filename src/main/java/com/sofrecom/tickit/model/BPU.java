package com.sofrecom.tickit.model;



import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class BPU {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String refTacheBPU;
	private String type_element;
	private String tarif;
	private float pu; 
	
	private String dateDebut;
	private String dateValidite;
	private String dateExpiration;
	@Column(name = "type_penalite")
	private String typePenalite;//variable ou fixe
	private String penalite;// pourc ou prix
	//private String type_prestation;
	private String version;	
	private boolean actif;
	
	
	 @ManyToOne
	 @JoinColumn(name = "id_prestation")
	 private Prestation typeprestation;
	
	
	
	public boolean isActif() {
		return actif;
	}
	public void setActif(boolean actif) {
		this.actif = actif;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getRefTacheBPU() {
		return refTacheBPU;
	}
	public void setRefTacheBPU(String refTacheBPU) {
		this.refTacheBPU = refTacheBPU;
	}
	public String getType_element() {
		return type_element;
	}
	public void setType_element(String type_element) {
		this.type_element = type_element;
	}
	public float getPU() {
		return pu;
	}
	public void setPU(float pU) {
		pu = pU;
	}
	public String getDateDebut() {
		return dateDebut;
	}
	public void setDateDebut(String dateDebut) {
		this.dateDebut = dateDebut;
	}
	public String getDateValidite() {
		return dateValidite;
	}
	public void setDateValidite(String dateValidite) {
		this.dateValidite = dateValidite;
	}
	public String getDateExpiration() {
		return dateExpiration;
	}
	public void setDateExpiration(String dateExpiration) {
		this.dateExpiration = dateExpiration;
	}
	
	
	public String getTypePenalite() {
		return typePenalite;
	}
	public void setTypePenalite(String typePenalite) {
		this.typePenalite = typePenalite;
	}
	public String getPenalite() {
		return penalite;
	}
	public void setPenalite(String penalite) {
		this.penalite = penalite;
	}
	
	
	public Prestation getTypeprestation() {
		return typeprestation;
	}
	public void setTypeprestation(Prestation typeprestation) {
		this.typeprestation = typeprestation;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public BPU() {
		super();
	}
		
	public String getTarif() {
		return tarif;
	}
	public void setTarif(String tarif) {
		this.tarif = tarif;
	}
	
	public BPU(long id, String refTacheBPU, String type_element, float pU, String tarif, String dateDebut,
			String dateValidite, String dateExpiration, String typePenalite, String penalite,
			Prestation typeprestation,String version) {
		super();
		this.id = id;
		this.refTacheBPU = refTacheBPU;
		this.type_element = type_element;
		pu = pU;
		this.tarif = tarif;
		this.dateDebut = dateDebut;
		this.dateValidite = dateValidite;
		this.dateExpiration = dateExpiration;
		this.typePenalite = typePenalite;
		this.penalite = penalite;
	    this.typeprestation = typeprestation; // Utilisation de la nouvelle relation
		this.version = version;
	}
	
	public BPU(String refTacheBPU, String type_element, float pU, String tarif, String dateDebut, String dateValidite,
			String dateExpiration, String type_penalite, String penalite, Prestation typeprestation, String version) {
		super();
		this.refTacheBPU = refTacheBPU;
		this.type_element = type_element;
		pu = pU;
		this.tarif = tarif;
		this.dateDebut = dateDebut;
		this.dateValidite = dateValidite;
		this.dateExpiration = dateExpiration;
		this.typePenalite = type_penalite;
		this.penalite = penalite;
	    this.typeprestation = typeprestation; // Utilisation de la nouvelle relation
		this.version = version;
	}
	
	
	
	@Override
	public String toString() {
		return "BPU [id=" + id + ", refTacheBPU=" + refTacheBPU + ", type_element=" + type_element + ", tarif=" + tarif
				+ ", pu=" + pu + ", dateDebut=" + dateDebut + ", dateValidite=" + dateValidite + ", dateExpiration="
				+ dateExpiration + ", type_penalite=" + typePenalite + ", penalite=" + penalite + ", version="
				+ version + ", actif=" + actif + ", typeprestation=" + typeprestation + "]";
	}
	// Méthode de mise à jour de l'entité BPU
    public void update(BPU bpuToUpdate) {
        this.typeprestation = bpuToUpdate.getTypeprestation();

        // Incrémenter la version
        this.version = this.version + 1;
    }
}
	
	
	
	

	
	
	
	
	
	
	
	
	
	
	
	

