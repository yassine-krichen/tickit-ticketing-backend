package com.sofrecom.tickit.model;

import javax.persistence.*;

@Entity
public class Motif {
	 @Id
	    @GeneratedValue
	    Integer idMotif;


	    private String descriptionMotif;

	    @ManyToOne
	    @JoinColumn(name="prestation_id")
	    Prestation typePrestation;

	    public Motif(){

	    }

	    public Motif(String descriptionMotif, Prestation typePrestation) {
	        this.descriptionMotif = descriptionMotif;
	        this.typePrestation = typePrestation;
	    }

	    public Integer getIdMotif() {
	        return idMotif;
	    }

	    public void setIdMotif(Integer idMotif) {
	        this.idMotif = idMotif;
	    }

	    public String getDescriptionMotif() {
	        return descriptionMotif;
	    }

	    public void setDescriptionMotif(String descriptionMotif) {
	        this.descriptionMotif = descriptionMotif;
	    }

	    public Prestation getTypePrestation() {
	        return typePrestation;
	    }

	    public void setTypePrestation(Prestation typePrestation) {
	        this.typePrestation = typePrestation;
	    }

}
