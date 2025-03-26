package com.sofrecom.tickit.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sofrecom.tickit.model.enumList.TypePhase;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Phase extends Acte_traitement {



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dommage_id", nullable = false)
    @JsonBackReference
    private Dommage dommage;
    private String status = "En cours";

    private String typePhase;



    private String etat;

    private LocalDate dateMaj;

    private String causeMaj;

    private LocalDate dateEdition;

    @PrePersist
    @PreUpdate
    public void validatePhaseType() {
        if (typePhase == null || typePhase.isEmpty()) {
            throw new IllegalArgumentException("Phase type must be specified.");
        }
        try {
            TypePhase.valueOf(typePhase);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid phase type: " + typePhase);
        }
    }
}
