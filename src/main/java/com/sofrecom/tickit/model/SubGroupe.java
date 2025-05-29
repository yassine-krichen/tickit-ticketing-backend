package com.sofrecom.tickit.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@Entity
public class SubGroupe implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nom;
    private String abbreviation;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "subgroupe_equipe",
            joinColumns = @JoinColumn(name = "subgroupe_id"),
            inverseJoinColumns = @JoinColumn(name = "equipe_id")
    )
    private List<Equipe> equipes;

    @OneToMany(mappedBy = "subGroupe", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    //@JsonManagedReference
    @JsonIgnore
    private List<TypeTravail> typeTravails;

    @ManyToMany(mappedBy = "subGroupes", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Types> types;

    public List<Types> getTypes() {
        return types;
    }

    public void setTypes(List<Types> types) {
        this.types = types;
    }

    public SubGroupe(String nom, String abbreviation) {
        this.nom = nom;
        this.abbreviation = abbreviation;
    }

    public SubGroupe(String nom, String abbreviation, List<Equipe> equipes, List<TypeTravail> typeTravails) {
        this.nom = nom;
        this.abbreviation = abbreviation;
        this.equipes = equipes;
        this.typeTravails = typeTravails;
    }

    public SubGroupe() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public List<Equipe> getEquipes() {
        return equipes;
    }

    public void setEquipes(List<Equipe> equipes) {
        this.equipes = equipes;
    }

    public List<TypeTravail> getTypeTravails() {
        return typeTravails;
    }

    public void setTypeTravails(List<TypeTravail> typeTravails) {
        this.typeTravails = typeTravails;
    }

}

