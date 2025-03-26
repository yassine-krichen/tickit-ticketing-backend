package com.sofrecom.tickit.model;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import java.util.Date;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class FacturationTechnicien extends Acte_traitement {

    private String nd;
    private String uic;
    private String type;

    private boolean active=true;

    public FacturationTechnicien() {
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getNd() {
        return nd;
    }

    public void setNd(String nd) {
        this.nd = nd;
    }

    public String getUic() {
        return uic;
    }

    public void setUic(String uic) {
        this.uic = uic;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public FacturationTechnicien(Prestation typeprestation, String type_element, int quantite, Date dateReception, Date dateLivraison, String affectation, int duree, String commentaire, String motif, String statutFacturation, Date dateDeadline, String priorite, String nd, String uic, String type, boolean active) {
        super ( typeprestation, type_element, quantite, dateReception, dateLivraison, affectation, duree, commentaire, motif, statutFacturation, dateDeadline, priorite );
        this.nd = nd;
        this.uic = uic;
        this.type = type;
        this.active = active;
    }
}
