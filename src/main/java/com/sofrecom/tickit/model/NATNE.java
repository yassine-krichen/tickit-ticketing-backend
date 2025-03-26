package com.sofrecom.tickit.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class NATNE  extends Acte_traitement{

    private int appelRecus;
    private int appelServis;
    private String dmt;

    public NATNE(int appelRecus, int appelServis, String dmt) {
        this.appelRecus = appelRecus;
        this.appelServis = appelServis;
        this.dmt = dmt;
    }

    public NATNE(Prestation typeprestation, String type_element, int quantite, Date dateReception, Date dateLivraison, String affectation, int duree, String commentaire, String motif, String statutFacturation, Date dateDeadline, String priorite, int appelRecus, int appelServis, String dmt) {
        super ( typeprestation, type_element, quantite, dateReception, dateLivraison, affectation, duree, commentaire, motif, statutFacturation, dateDeadline, priorite );
        this.appelRecus = appelRecus;
        this.appelServis = appelServis;
        this.dmt = dmt;
    }
}
