package com.sofrecom.tickit.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class SousEquipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinTable(
            name = "collaborateur_sous_equipe",
            joinColumns = @JoinColumn(name = "sous_equipe_id"),
            inverseJoinColumns = @JoinColumn(name = "collaborateur_id")
    )
    private List<Collaborateur> collaborateurs = new ArrayList<>();




    // Getters et Setters


    public List<Collaborateur> getCollaborateurs() {
        return collaborateurs;
    }

    public void setCollaborateurs(List<Collaborateur> collaborateurs) {
        this.collaborateurs = collaborateurs;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }


}
