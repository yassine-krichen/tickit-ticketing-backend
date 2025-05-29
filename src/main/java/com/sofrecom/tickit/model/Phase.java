package com.sofrecom.tickit.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sofrecom.tickit.model.enumList.Status;
import com.sofrecom.tickit.model.enumList.TypePhase;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class Phase extends Acte_traitement {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dommage_id", nullable = false)
    @JsonBackReference
    @NotNull(message = "Dommage must not be null.")
    private Dommage dommage;

    @NotBlank(message = "Status must not be blank.")
    private String status = Status.A_TRAITER.toString ();

    @NotBlank(message = "TypePhase must not be blank.")
    private String typePhase;

    @Size(max = 255, message = "Etat must not exceed 255 characters.")
    private String etat;
    @Temporal(TemporalType.DATE)
    private Date dateMaj;


    private String causeMaj;
    @Temporal(TemporalType.DATE)
    private Date dateEdition;

    private Boolean isConfirmed = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phase_parent_id")
    @JsonIgnore
    private Phase phaseParent;

    @Temporal(TemporalType.DATE)
    private Date datePassage;

    @Temporal(TemporalType.DATE)
    private Date ancienDatePassage ;
    @Size(max = 50)
    private String ancienStatus = "";

    private int ancienDuree = 0;

    @OneToMany(mappedBy = "phaseParent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Phase> phasesAnnexes = new ArrayList<> ();



    @PrePersist
    @PreUpdate
    public void validatePhaseType() {
        if ( typePhase.equals ( "" ) || typePhase.isEmpty () ) {
            throw new IllegalArgumentException("Phase type must be specified.");
        }
        try {
            TypePhase.valueOf(typePhase);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid phase type: " + typePhase);
        }
    }
    public String getNextPhase() {
        if (TypePhase.CLOTURE_DOSSIER.name().equals(typePhase)) {
            return null;
        }

        int currentIndex = PhaseOrder.PHASE_TYPES_ORDRE.indexOf(TypePhase.valueOf(typePhase));
        if (currentIndex + 1 < PhaseOrder.PHASE_TYPES_ORDRE.size()) {
            return PhaseOrder.PHASE_TYPES_ORDRE.get(currentIndex + 1).name();
        } else {
            return null;
        }
    }

    public void setStatus(String status) {
        this.ancienStatus = this.status;
        this.status = status;
    }
    public void setDatePassage(Date datePassage) {
        this.ancienDatePassage = this.datePassage;
        this.datePassage = datePassage;
    }

    @Override
    public void setDuree(int duree) {
        this.ancienDuree = super.getDuree();
        super.setDuree(duree);
    }

    public Phase() {
    }

    public Phase(Date dateReception, Date dateLivraison, String affectation, int duree, String commentaire, String statutFacturation, String typePhase, String etat, Date dateMaj, String causeMaj, Date dateEdition, Date datePassage, Date ancienDatePassage) {
        super(dateReception,dateLivraison,affectation,duree,commentaire,statutFacturation);
        this.typePhase = typePhase;
        this.etat = etat;
        this.dateMaj = dateMaj;
        this.causeMaj = causeMaj;
        this.dateEdition = dateEdition;
        this.datePassage = datePassage;
        this.ancienDatePassage = ancienDatePassage;

    }
}
