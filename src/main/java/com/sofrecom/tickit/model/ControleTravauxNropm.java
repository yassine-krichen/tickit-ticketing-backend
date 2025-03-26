package com.sofrecom.tickit.model;

import javax.persistence.Entity;
import java.util.Date;

@Entity
public class ControleTravauxNropm extends Acte_traitement {
    private String ui;
    private String cog;
    private String typeActivite;
    private String motifReaffectation;

    public ControleTravauxNropm() {

    }

    public ControleTravauxNropm(String idacte, Prestation prestation, String typeElement, int quantite,
                                Date dateReception, Date dateLivraison,String affectation, int duree,
                                String commentaire, String motif, String statutFacturation, String Priorite, String classification,
                                Date dateDealine, String ui, String cog,String motifReaffectation, String typeActivite) {

        super(idacte, prestation, typeElement, quantite,dateReception,
                dateLivraison,affectation, duree,commentaire,motif, statutFacturation,
                Priorite, classification, dateDealine);
        this.ui = ui;
        this.cog = cog;
        this.typeActivite=typeActivite;
        this.motifReaffectation = motifReaffectation;
    }


    public ControleTravauxNropm(String cog, String motif, String typeElement, String commentaire,
                                Prestation pres, String statutFacturation, Date dateReception,
                                Date dateDeadline, String ui) {
        super(motif,typeElement, commentaire, pres,statutFacturation,dateReception ,dateDeadline);
        this.ui = ui;
        this.cog = cog;
    }

    public String getUi() {
        return ui;
    }

    public void setUi(String ui) {
        this.ui = ui;
    }

    public String getCog() {
        return cog;
    }

    public void setCog(String cog) {
        this.cog = cog;
    }

    public String getTypeActivite() {
        return typeActivite;
    }

    public void setTypeActivite(String typeActivite) {
        this.typeActivite = typeActivite;
    }

    public String getMotifReaffectation() {
        return motifReaffectation;
    }

    public void setMotifReaffectation(String motifReaffectation) {
        this.motifReaffectation = motifReaffectation;
    }
}
