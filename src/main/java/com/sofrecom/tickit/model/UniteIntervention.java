package com.sofrecom.tickit.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class UniteIntervention {
	 @Id
	    @GeneratedValue
	    Integer id_ui;
	    
	    private String description;
	    @ManyToOne
	    @JoinColumn(name="prestation_id")
	    Prestation typePrestation;
		public Integer getId_ui() {
			return id_ui;
		}
		public void setId_ui(Integer id_ui) {
			this.id_ui = id_ui;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public Prestation getTypePrestation() {
			return typePrestation;
		}
		public void setTypePrestation(Prestation typePrestation) {
			this.typePrestation = typePrestation;
		}
		public UniteIntervention(Integer id_ui, String description, Prestation typePrestation) {
			super();
			this.id_ui = id_ui;
			this.description = description;
			this.typePrestation = typePrestation;
		}
		public UniteIntervention() {
		}
	    
	    
}
