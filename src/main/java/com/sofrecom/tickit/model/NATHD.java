package com.sofrecom.tickit.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class NATHD  extends  Acte_traitement{

    private int appelRecus;
    private int appelServis;
    private String dmt;


    public int getAppelRecus() {
        return appelRecus;
    }

    public void setAppelRecus(int appelRecus) {
        this.appelRecus = appelRecus;
    }

    public int getAppelServis() {
        return appelServis;
    }

    public void setAppelServis(int appelServis) {
        this.appelServis = appelServis;
    }

    public String getDmt() {
        return dmt;
    }

    public void setDmt(String dmt) {
        this.dmt = dmt;
    }

    public NATHD(Prestation typeprestation, String type_element, int quantite, Date dateReception, Date dateLivraison, String affectation, int duree, String commentaire, String motif, String statutFacturation, Date dateDeadline, String priorite, int appelRecus, int appelServis, String dmt) {
        super ( typeprestation, type_element, quantite, dateReception, dateLivraison, affectation, duree, commentaire, motif, statutFacturation, dateDeadline, priorite );
        this.appelRecus = appelRecus;
        this.appelServis = appelServis;
        this.dmt = dmt;
    }
}
