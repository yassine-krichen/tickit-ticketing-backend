package com.sofrecom.tickit.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.List;

@Entity
public class Types {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nom;
    private String abbreviation;
    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "types_subgroupe",
            joinColumns = @JoinColumn(name = "types_id"),
            inverseJoinColumns = @JoinColumn(name = "subgroupe_id")
    )
    private List<SubGroupe> subGroupes;

    @OneToMany(mappedBy = "types", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    //@JsonManagedReference
    @JsonIgnore
    private List<TypeTravail> typeTravails;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<SubGroupe> getSubGroupes() {
        return subGroupes;
    }

    public void setSubGroupes(List<SubGroupe> subGroupes) {
        this.subGroupes = subGroupes;
    }

    public List<TypeTravail> getTypeTravails() {
        return typeTravails;
    }

    public void setTypeTravails(List<TypeTravail> typeTravails) {
        this.typeTravails = typeTravails;
    }
}
