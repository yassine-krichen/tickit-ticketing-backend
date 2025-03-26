package com.sofrecom.tickit.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
public class Planning implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer planningId;
    @Temporal(TemporalType.DATE)
    private Date date ;

    private Integer journee;

    private boolean formation;


    @ManyToOne
    @JoinColumn(name = "typeTravail")
    private TypeTravail typeTravail;

    @ManyToOne
    @JoinColumn(name = "CUID")
    private Collaborateur collaborateur;


    public Integer getPlanningId() {
        return planningId;
    }

    public boolean isFormation() {
        return formation;
    }

    public void setFormation(boolean formation) {
        this.formation = formation;
    }

    public void setPlanningId(Integer planningId) {
        this.planningId = planningId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public TypeTravail getTypeTravail() {
        return typeTravail;
    }

    public void setTypeTravail(TypeTravail typeTravail) {
        this.typeTravail = typeTravail;
    }

    public Collaborateur getCollaborateur() {
        return collaborateur;
    }

    public void setCollaborateur(Collaborateur collaborateur) {
        this.collaborateur = collaborateur;
    }

    public Integer getJournee() {
        return journee;
    }

    public void setJournee(Integer journee) {
        this.journee = journee;
    }

    public Planning(Integer planningId, Date date, Integer journee, boolean formation, TypeTravail typeTravail, Collaborateur collaborateur) {
        this.planningId = planningId;
        this.date = date;
        this.journee = journee;
        this.formation = formation;
        this.typeTravail = typeTravail;
        this.collaborateur = collaborateur;
    }

    public Planning() {
    }

    @Override
    public String toString() {
        return "Planning{" +
                "planningId=" + planningId +
                ", date=" + date +
                ", journee=" + journee +
                ", formation=" + formation +
                ", typeTravail=" + typeTravail +
                ", collaborateur=" + collaborateur +
                '}';
    }
}
