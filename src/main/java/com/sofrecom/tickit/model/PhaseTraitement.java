package com.sofrecom.tickit.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sofrecom.tickit.model.enumList.TypeTraitement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "phase_traitement")
public class PhaseTraitement  extends  Acte_traitement{


    private Long compteur;


    @Enumerated(EnumType.STRING)
    private TypeTraitement typeTraitement;

    @Column(name = "montant", nullable = true)
    private Float montant;


    private String etat;
    @Temporal(TemporalType.DATE)
    private Date  dateMaj;
    private String causeMaj;
    @Temporal(TemporalType.DATE)
    private Date dateEdition;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "dommage_id")
    @JsonBackReference
    private Dommage dommage;

    public boolean canProceedToNextPhase() {
        if (typeTraitement == TypeTraitement.AFFECTATION_ET_MISE_EN_CAUSE) {
            return true;
        }
        if (typeTraitement == TypeTraitement.CLOTURE_DOSSIER) {
            setDateLivraison(new Date());
        }
        return getDateLivraison () != null;
    }
    public TypeTraitement getNextPhase() {
        if (getDateLivraison () == null) {
            return null;
        }

        for (TypeTraitement tt : TypeTraitement.values()) {
            if (tt.getStep() == this.typeTraitement.getStep() + 1) {
                return tt;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "PhaseTraitement{" +
                "compteur=" + compteur +
                ", typeTraitement=" + typeTraitement +
                ", montant=" + montant +
                ", etat='" + etat + '\'' +
                ", dateMaj=" + dateMaj +
                ", causeMaj='" + causeMaj + '\'' +
                ", dateEdition=" + dateEdition +
                ", dommage=" + dommage +
                '}';
    }


}