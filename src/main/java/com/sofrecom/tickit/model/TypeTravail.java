package com.sofrecom.tickit.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
public class TypeTravail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer typeTravailId;
    private String nom ;
    private String colorCode;
    private String abbreviation;
    private String formationColorCode;

    @ManyToOne
    @JoinColumn(name = "subgroupe_id", nullable = true)
    @JsonIgnoreProperties("typeTravails")
    private SubGroupe subGroupe;
    @ManyToOne
    @JoinColumn(name = "types_id")
    //@JsonBackReference
    private Types types;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "type_travail_equipe",
            joinColumns = @JoinColumn(name = "type_travail_id"),
            inverseJoinColumns = @JoinColumn(name = "equipe_id")
    )
    private List<Equipe> equipes;

    public Types getTypes() {
        return types;
    }

    public void setTypes(Types types) {
        this.types = types;
    }

    public Integer getTypeTravailId() {
        return typeTravailId;
    }

    public void setTypeTravailId(Integer typeTravailId) {
        this.typeTravailId = typeTravailId;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }


    public TypeTravail(String nom, String colorCode, String abbreviation) {
        this.nom = nom;
        this.colorCode = colorCode;
        this.abbreviation = abbreviation;
        this.formationColorCode = formationColorCode;
    }

    public TypeTravail() {
    }

    @Override
    public String toString() {
        return "TypeTravail{" +
                "typeTravailId=" + typeTravailId +
                ", nom='" + nom + '\'' +
                ", colorCode='" + colorCode + '\'' +
                ", abbreviation='" + abbreviation + '\'' +
                ", subGroupe=" + subGroupe +
                '}';
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public String getFormationColorCode() {
        return formationColorCode;
    }

    public void setFormationColorCode(String formationColorCode) {
        this.formationColorCode = formationColorCode;
    }
    @PrePersist
    protected void onCreate() {
        if (colorCode == null) {
            colorCode = "#FFFFFF"; // Default to white
        }
        if (formationColorCode == null) {
            formationColorCode = "#FFFFFF"; // Default to white
        }
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public SubGroupe getSubGroupe() {
        return subGroupe;
    }

    public void setSubGroupe(SubGroupe subGroupe) {
        this.subGroupe = subGroupe;
    }

}
