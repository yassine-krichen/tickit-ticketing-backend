package com.sofrecom.tickit.model;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@ToString
public class Dommage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String region;

    private String ndi;

    private double montant;

    private LocalDate dateLivraison;

    @Transient
    private int dureeTraitement;

    @ToString.Exclude
    @OneToMany(mappedBy = "dommage", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Phase> phases = new ArrayList<> ();



    @PostLoad
    @PostPersist
    @PostUpdate
    public void calculateDureeTraitement() {
        if ( phases.isEmpty() ) {
            this.dureeTraitement = 0;
        }else {
            this.dureeTraitement = phases.stream().mapToInt(Phase::getDuree).sum();
        }
    }

    

    public Dommage() {
    }
}
