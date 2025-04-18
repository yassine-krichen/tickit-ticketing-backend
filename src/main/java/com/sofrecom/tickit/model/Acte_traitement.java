package com.sofrecom.tickit.model;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.stereotype.Indexed;

import javax.persistence.*;
import java.util.Date;


@Indexed
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "acte_traitement")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "idacte")
public class Acte_traitement {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "idacte")
    private String idacte;

    @Column(name = "ref_tachebpu")
    private String refTacheBPU;


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "idPrestation")
    Prestation typeprestation;


    @Column(name = "type_element")
    private String type_element;

    @Column(name = "quantite")
    private int quantite;


    @Column(name = "date_reception")
    @Temporal(TemporalType.DATE)
    private Date dateReception;

    @Column(name = "date_livraison")
    @Temporal(TemporalType.DATE)

    private Date dateLivraison;

    @Column(name = "date_validation")
    private Date dateValidation;

    @Column(name = "affectation")
    private String affectation;

    @Column(name = "duree")
    private int duree;

    @Column(name = "commentaire")
    private String commentaire;

    @Column(name = "motif")
    private String motif;

    @Column(name = "statut_facturation")
    private String statutFacturation;

    @Column(name = "date_reprise")
    private Date dateReprise;

    @Column(name = "reprise_facturable")
    private String repriseFacturable;
    @Temporal(TemporalType.DATE)
    private Date dateDeadline;
    private String priorite = "P1";

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "codeTarif")
    Tarification tarif;


    @Column(name = "DateFacturation")
    private Date DateFacturation;

    @Column(name = "isDeleted")
    private boolean isDeleted;
    @Column(name = "classification")
    private String classification;

    /**
     * The case this treatment act belongs to.
     */
    @ManyToOne(fetch = FetchType.EAGER) // Changed to EAGER loading
    @JoinColumn(name = "case_id")
    private Case associatedCase;

    public Case getAssociatedCase() {
        return associatedCase;
    }

    public void setAssociatedCase(Case associatedCase) {
        this.associatedCase = associatedCase;
    }
    // TODO: see how to fit in the associatedCase in the constructor

    public Acte_traitement(Date dateReception, Date dateLivraison, Date dateValidation,
                           String affectation, int duree, String commentaire, String motif,
                           String statutFacturation, Date dateDeadline, String priorite) {
        this.dateReception= dateReception;
        this.dateLivraison= dateLivraison;
        this.dateValidation=dateValidation;
        this.affectation=affectation;
        this.duree=duree;
        this.commentaire=commentaire;
        this.motif=motif;
        this.statutFacturation=statutFacturation;
        this.dateDeadline=dateDeadline;
        this.priorite=priorite;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public Date getDateFacturation() {
        return DateFacturation;
    }

    public void setDateFacturation(Date dateFacturation) {
        DateFacturation = dateFacturation;
    }

    public String getIdacte() {
        return idacte;
    }

    public void setIdacte(String idacte) {
        this.idacte = idacte;
    }

    public String getRefTacheBPU() {
        return refTacheBPU;
    }

    public void setRefTacheBPU(String refTacheBPU) {
        this.refTacheBPU = refTacheBPU;
    }

    public Prestation getType_prestation() {
        return typeprestation;
    }

    public void setType_prestation(Prestation type_prestation) {
        this.typeprestation = type_prestation;
    }

    public String getType_element() {
        return type_element;
    }

    public void setType_element(String type_element) {
        this.type_element = type_element;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }


    public Date getDateLivraison() {
        return dateLivraison;
    }

    public void setDateLivraison(Date dateLivraison) {
        this.dateLivraison = dateLivraison;
    }


    public Date getDateReception() {
        return dateReception;
    }

    public void setDateReception(Date dateReception) {
        this.dateReception = dateReception;
    }

    public Date getDateValidation() {
        return dateValidation;
    }

    public void setDateValidation(Date dateValidation) {
        this.dateValidation = dateValidation;
    }

    public String getAffectation() {
        return affectation;
    }

    public void setAffectation(String affectation) {
        this.affectation = affectation;
    }

    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public String getStatutFacturation() {
        return statutFacturation;
    }

    public void setStatutFacturation(String statutFacturation) {
        this.statutFacturation = statutFacturation;
    }

    public Date getDateReprise() {
        return dateReprise;
    }

    public void setDateReprise(Date dateReprise) {
        this.dateReprise = dateReprise;
    }

    public String getRepriseFacturable() {
        return repriseFacturable;
    }

    public void setRepriseFacturable(String repriseFacturable) {
        this.repriseFacturable = repriseFacturable;
    }

    public Date getDateDeadline() {
        return dateDeadline;
    }

    public void setDateDeadline(Date dateDeadline) {
        this.dateDeadline = dateDeadline;
    }

    public String getPriorite() {
        return priorite;
    }

    public void setPriorite(String priorite) {
        this.priorite = priorite;
    }

    public Tarification getTarif() {
        return tarif;
    }

    public void setTarif(Tarification tarif) {
        this.tarif = tarif;
    }

    public Acte_traitement(String idacte, String refTacheBPU, Prestation type_prestation, String type_element,
                           int quantite, Date dateReception, Date dateLivraison, Date dateValidation, String affectation, int duree,
                           String commentaire, String motif, String statutFacturation, Date dateReprise, String repriseFacturable,
                           Date dateDeadline, String priorite, Tarification tarif) {
        super ();
        this.idacte = idacte;
        this.refTacheBPU = refTacheBPU;
        this.typeprestation = type_prestation;
        this.type_element = type_element;
        this.quantite = quantite;
        this.dateReception = dateReception;
        this.dateLivraison = dateLivraison;
        this.dateValidation = dateValidation;
        this.affectation = affectation;
        this.duree = duree;
        this.commentaire = commentaire;
        this.motif = motif;
        this.statutFacturation = statutFacturation;
        this.dateReprise = dateReprise;
        this.repriseFacturable = repriseFacturable;
        this.dateDeadline = dateDeadline;
        this.priorite = priorite;
        this.tarif = tarif;
    }


    public Acte_traitement(Prestation type_prestation, String type_element, int quantite, String statutFacturation, String motif, Date dateReception) {
        this.typeprestation = type_prestation;
        this.type_element = type_element;
        this.quantite = quantite;
        this.statutFacturation = statutFacturation;
        this.motif = motif;
        this.dateReception = dateReception;
    }

    public Acte_traitement(Prestation type_prestation, String type_element, int quantite, Date dateReception, Date dateLivraison, Date dateValidation, String affectation, int duree, String commentaire, String motif, String statutFacturation) {
        this.typeprestation = type_prestation;
        this.type_element = type_element;
        this.quantite = quantite;
        this.dateReception = dateReception;
        this.dateLivraison = dateLivraison;
        this.dateValidation = dateValidation;
        this.affectation = affectation;
        this.duree = duree;
        this.commentaire = commentaire;
        this.motif = motif;
        this.statutFacturation = statutFacturation;
    }


    public Acte_traitement(String refTacheBPU, Prestation type_prestation, String type_element, int quantite,
                           Date dateReception, Date dateLivraison, Date dateValidation, String affectation, int duree,
                           String commentaire, String motif) {
        super ();
        this.refTacheBPU = refTacheBPU;
        this.typeprestation = type_prestation;
        this.type_element = type_element;
        this.quantite = quantite;
        this.dateReception = dateReception;
        this.dateLivraison = dateLivraison;
        this.dateValidation = dateValidation;
        this.affectation = affectation;
        this.duree = duree;
        this.commentaire = commentaire;
        this.motif = motif;

    }


    public Acte_traitement(String idacte) {
        this.idacte = idacte;
    }

    public Acte_traitement(Prestation type_prestation, String type_element, int quantite, String statutFacturation, String motif, Date dateReception, Date dateDeadline) {
        this.typeprestation = type_prestation;
        this.type_element = type_element;
        this.quantite = quantite;
        this.statutFacturation = statutFacturation;
        this.motif = motif;
        this.dateReception = dateReception;
        this.dateDeadline = dateDeadline;
    }


    public Acte_traitement(Prestation type_prestation, String type_element, int quantite, Date dateReception, Date dateLivraison, Date dateValidation, String affectation, int duree, String commentaire, String motif, String statutFacturation, Date dateDeadline, String priorite, String classification) {
        this.typeprestation = type_prestation;
        this.type_element = type_element;
        this.quantite = quantite;
        this.dateReception = dateReception;
        this.dateLivraison = dateLivraison;
        this.dateValidation = dateValidation;
        this.affectation = affectation;
        this.duree = duree;
        this.commentaire = commentaire;
        this.motif = motif;
        this.statutFacturation = statutFacturation;
        this.dateDeadline = dateDeadline;
        this.priorite = priorite;
        this.classification= classification;
    }
    
    //Reaff
    public Acte_traitement(Prestation type_prestation,  Date dateReception, Date dateLivraison, Date dateValidation, String affectation, int duree, String commentaire, String motif, String statutFacturation, Date dateDeadline, String priorite) {
        this.typeprestation = type_prestation;
        this.dateReception = dateReception;
        this.dateLivraison = dateLivraison;
        this.dateValidation = dateValidation;
        this.affectation = affectation;
        this.duree = duree;
        this.commentaire = commentaire;
        this.motif = motif;
        this.statutFacturation = statutFacturation;
        this.dateDeadline = dateDeadline;
        this.priorite = priorite;
    }
    //filtrage
    public Acte_traitement(Prestation type_prestation, String type_element,Tarification tarif, int quantite, Date dateReception, Date dateLivraison, Date dateValidation, String affectation, int duree, String commentaire, String motif, String statutFacturation, Date dateDeadline, String priorite, String classification) {
        this.typeprestation = type_prestation;
        this.type_element = type_element;
        this.tarif=tarif;
        this.quantite = quantite;
        this.dateReception = dateReception;
        this.dateLivraison = dateLivraison;
        this.dateValidation = dateValidation;
        this.affectation = affectation;
        this.duree = duree;
        this.commentaire = commentaire;
        this.motif = motif;
        this.statutFacturation = statutFacturation;
        this.dateDeadline = dateDeadline;
        this.priorite = priorite;
        this.classification=classification;
    }

    public Acte_traitement() {
        super ();
    }

    //brame
    public Acte_traitement(Prestation type_prestation, String type_element, int quantite, String statutFacturation, String motif, Date dateReception, String commentaire) {
        this.typeprestation = type_prestation;
        this.type_element = type_element;
        this.quantite = quantite;
        this.statutFacturation = statutFacturation;
        this.motif = motif;
        this.dateReception = dateReception;
        this.commentaire=commentaire;
    }

    public Acte_traitement(String idacte, String refTacheBPU, Prestation type_prestation,
                           String type_element, int quantite, Date dateReception, Date dateLivraison, Date dateValidation,
                           String affectation, int duree, String commentaire, String motif, String statutFacturation,
                           Date dateReprise, String repriseFacturable) {
        super ();
        this.idacte = idacte;
        this.refTacheBPU = refTacheBPU;
        this.typeprestation = type_prestation;
        this.type_element = type_element;
        this.quantite = quantite;
        this.dateReception = dateReception;
        this.dateLivraison = dateLivraison;
        this.dateValidation = dateValidation;
        this.affectation = affectation;
        this.duree = duree;
        this.commentaire = commentaire;
        this.motif = motif;
        this.statutFacturation = statutFacturation;
        this.dateReprise = dateReprise;
        this.repriseFacturable = repriseFacturable;
    }


    public Acte_traitement(String refTacheBPU, Prestation type_prestation, String type_element, int quantite,
                           Date dateReception, Date dateLivraison, Date dateValidation, String affectation, int duree,
                           String commentaire, String motif, String statutFacturation, Date dateReprise, String repriseFacturable) {
        super ();
        this.refTacheBPU = refTacheBPU;
        this.typeprestation = type_prestation;
        this.type_element = type_element;
        this.quantite = quantite;
        this.dateReception = dateReception;
        this.dateLivraison = dateLivraison;
        this.dateValidation = dateValidation;
        this.affectation = affectation;
        this.duree = duree;
        this.commentaire = commentaire;
        this.motif = motif;
        this.statutFacturation = statutFacturation;
        this.dateReprise = dateReprise;
        this.repriseFacturable = repriseFacturable;
    }

    @Override
    public String toString() {
        return "Acte_traitement [ idacte=" + idacte + ", refTacheBPU=" + refTacheBPU
                + ", type_prestation=" + typeprestation + ", type_element=" + type_element + ", quantite=" + quantite
                + ", dateReception=" + dateReception + ", dateLivraison=" + dateLivraison + ", dateValidation="
                + dateValidation + ", affectation=" + affectation + ", duree=" + duree + ", commentaire=" + commentaire
                + ", motif=" + motif + ", statutFacturation=" + statutFacturation + ", dateReprise=" + dateReprise
                + ", repriseFacturable=" + repriseFacturable + "]";
    }


    public Acte_traitement(String idacte, String refTacheBPU, Prestation type_prestation, String type_element, int quantite,
                           Date dateReception, Date dateLivraison, Date dateValidation, String affectation, Date dateDeadline,
                           String priorite, int duree, String commentaire, String motif, String statutFacturation, Date dateReprise,
                           String repriseFacturable, String classification) {
        super ();
        this.idacte = idacte;
        this.refTacheBPU = refTacheBPU;
        this.typeprestation = type_prestation;
        this.type_element = type_element;
        this.quantite = quantite;
        this.dateReception = dateReception;
        this.dateLivraison = dateLivraison;
        this.dateValidation = dateValidation;
        this.affectation = affectation;
        this.dateDeadline = dateDeadline;
        this.priorite = priorite;
        this.duree = duree;
        this.commentaire = commentaire;
        this.motif = motif;
        this.statutFacturation = statutFacturation;
        this.dateReprise = dateReprise;
        this.repriseFacturable = repriseFacturable;
        this.classification=classification;
    }
//Grafic
    public Acte_traitement(String idacte, String refTacheBPU, Prestation type_prestation, String type_element, int quantite,
                           Date dateReception, Date dateLivraison, Date dateValidation, String affectation, Date dateDeadline,
                           String priorite, int duree, String commentaire, String motif, String statutFacturation, Date dateReprise,
                           String repriseFacturable) {
        super ();
        this.idacte = idacte;
        this.refTacheBPU = refTacheBPU;
        this.typeprestation = type_prestation;
        this.type_element = type_element;
        this.quantite = quantite;
        this.dateReception = dateReception;
        this.dateLivraison = dateLivraison;
        this.dateValidation = dateValidation;
        this.affectation = affectation;
        this.dateDeadline = dateDeadline;
        this.priorite = priorite;
        this.duree = duree;
        this.commentaire = commentaire;
        this.motif = motif;
        this.statutFacturation = statutFacturation;
        this.dateReprise = dateReprise;
        this.repriseFacturable = repriseFacturable;
    }


    public Acte_traitement(String motif, String type_element, String commentaire, Prestation type_prestation) {
        super ();
        this.typeprestation = type_prestation;
        this.type_element = type_element;
        this.commentaire = commentaire;
        this.motif = motif;
    }

    public Acte_traitement(String refTacheBPU, Prestation type_prestation, String type_element, int quantite,
                           Date dateReception, Date dateLivraison, Date dateValidation, String affectation, int duree,
                           String commentaire, String motif, Date dateDeadline, String priorite) {
        super ();
        this.refTacheBPU = refTacheBPU;
        this.typeprestation = type_prestation;
        this.type_element = type_element;
        this.quantite = quantite;
        this.dateReception = dateReception;
        this.dateLivraison = dateLivraison;
        this.dateValidation = dateValidation;
        this.affectation = affectation;
        this.duree = duree;
        this.commentaire = commentaire;
        this.motif = motif;
        this.dateDeadline = dateDeadline;
        this.priorite = priorite;

    }


    public Acte_traitement(String idacte, String refTacheBPU) {
        super ();
        this.idacte = idacte;
        this.refTacheBPU = refTacheBPU;
    }


    public Acte_traitement(String motif, String type_element, String commentaire, Prestation type_prestation, int quantite, String statutFacturation, Date dateReception, Date dateDeadline) {
        super ();
        this.typeprestation = type_prestation;
        this.type_element = type_element;
        this.commentaire = commentaire;
        this.motif = motif;
        this.quantite = quantite;
        this.statutFacturation = statutFacturation;
        this.dateReception = dateReception;
        this.dateDeadline = dateDeadline;
    }


    public Acte_traitement(String refTacheBPU, Prestation type_prestation, String type_element, int quantite,
                           Date dateReception, Date dateLivraison, Date dateValidation, String affectation, int duree,
                           String commentaire, String motif, Date dateDeadline, String priorite, String StatutFacturation) {
        super ();
        this.refTacheBPU = refTacheBPU;
        this.typeprestation = type_prestation;
        this.type_element = type_element;
        this.quantite = quantite;
        this.dateReception = dateReception;
        this.dateLivraison = dateLivraison;
        this.dateValidation = dateValidation;
        this.affectation = affectation;
        this.duree = duree;
        this.commentaire = commentaire;
        this.motif = motif;
        this.dateDeadline = dateDeadline;
        this.priorite = priorite;
        this.statutFacturation = StatutFacturation;

    }

    public Acte_traitement(String idacte, String refTacheBPU, Prestation type_prestation, String type_element, int quantite,
                           Date dateReception, Date dateLivraison, Date dateValidation, String affectation, int duree,
                           String commentaire, String motif, String statutFacturation, Date dateReprise, String repriseFacturable,
                           Date dateDeadline, String priorite, String classification) {
        super ();
        this.idacte = idacte;
        this.refTacheBPU = refTacheBPU;
        this.typeprestation = type_prestation;
        this.type_element = type_element;
        this.quantite = quantite;
        this.dateReception = dateReception;
        this.dateLivraison = dateLivraison;
        this.dateValidation = dateValidation;
        this.affectation = affectation;
        this.duree = duree;
        this.commentaire = commentaire;
        this.motif = motif;
        this.statutFacturation = statutFacturation;
        this.dateReprise = dateReprise;
        this.repriseFacturable = repriseFacturable;
        this.dateDeadline = dateDeadline;
        this.priorite = priorite;
        this.classification=classification;
    }

//XOIP
    public Acte_traitement(String idacte, String refTacheBPU, Prestation type_prestation, String type_element, int quantite,
                           Date dateReception, Date dateLivraison, Date dateValidation, String affectation, int duree,
                           String commentaire, String motif, String statutFacturation, Date dateReprise, String repriseFacturable,
                           Date dateDeadline, String priorite) {
        super ();
        this.idacte = idacte;
        this.refTacheBPU = refTacheBPU;
        this.typeprestation = type_prestation;
        this.type_element = type_element;
        this.quantite = quantite;
        this.dateReception = dateReception;
        this.dateLivraison = dateLivraison;
        this.dateValidation = dateValidation;
        this.affectation = affectation;
        this.duree = duree;
        this.commentaire = commentaire;
        this.motif = motif;
        this.statutFacturation = statutFacturation;
        this.dateReprise = dateReprise;
        this.repriseFacturable = repriseFacturable;
        this.dateDeadline = dateDeadline;
        this.priorite = priorite;
    }

    //Reaff desat
    public Acte_traitement( Prestation type_prestation, String type_element, int quantite,
                            Date dateReception, Date dateLivraison, Date dateValidation, String affectation, int duree,
                            String commentaire, String motif, String statutFacturation, Date dateReprise, String repriseFacturable,
                            Date dateDeadline, String priorite) {
        super ();
        this.typeprestation = type_prestation;
        this.type_element = type_element;
        this.quantite = quantite;
        this.dateReception = dateReception;
        this.dateLivraison = dateLivraison;
        this.dateValidation = dateValidation;
        this.affectation = affectation;
        this.duree = duree;
        this.commentaire = commentaire;
        this.motif = motif;
        this.statutFacturation = statutFacturation;
        this.dateReprise = dateReprise;
        this.repriseFacturable = repriseFacturable;
        this.dateDeadline = dateDeadline;
        this.priorite = priorite;
    }

    public Acte_traitement(String idacte, Prestation type_prestation) {
        super ();
        this.idacte = idacte;
        this.typeprestation = type_prestation;
    }

    public Acte_traitement(String idacte, Date dateReception, String affectation, int duree, String commentaire) {
        super ();
        this.idacte = idacte;
        this.dateReception = dateReception;
        this.affectation = affectation;
        this.duree = duree;
        this.commentaire = commentaire;
    }

    public Acte_traitement(String refTacheBPU, Prestation type_prestation, String type_element, int quantite,
                           Date dateReception, Date dateLivraison, Date dateValidation, String affectation, int duree,
                           String commentaire, String motif, String statutFacturation, Date dateReprise, String repriseFacturable,
                           Date dateDeadline, String priorite, Tarification tarif) {
        super ();
        this.refTacheBPU = refTacheBPU;
        this.typeprestation = type_prestation;
        this.type_element = type_element;
        this.quantite = quantite;
        this.dateReception = dateReception;
        this.dateLivraison = dateLivraison;
        this.dateValidation = dateValidation;
        this.affectation = affectation;
        this.duree = duree;
        this.commentaire = commentaire;
        this.motif = motif;
        this.statutFacturation = statutFacturation;
        this.dateReprise = dateReprise;
        this.repriseFacturable = repriseFacturable;
        this.dateDeadline = dateDeadline;
        this.priorite = priorite;
        this.tarif = tarif;
    }

    public Acte_traitement(Prestation typeprestation, String type_element, int quantite, Date dateReception, Date dateLivraison, String affectation, int duree, String commentaire, String motif, String statutFacturation, String priorite, Tarification tarif, Date dateFacturation) {

        this.typeprestation = typeprestation;
        this.type_element = type_element;
        this.quantite = quantite;
        this.dateReception = dateReception;
        this.dateLivraison = dateLivraison;
        this.affectation = affectation;
        this.duree = duree;
        this.commentaire = commentaire;
        this.motif = motif;
        this.statutFacturation = statutFacturation;
        this.priorite = priorite;
        this.tarif = tarif;
        this.DateFacturation = dateFacturation;
    }

    public Acte_traitement(String idacte, Prestation typeprestation, String type_element,
                           int quantite, Date dateReception, Date dateLivraison, String affectation, Date dateDeadline,
                           String commentaire, int duree,
                           String motif, String statutFacturation, String repriseFacturable, Date dateReprise,
                           String priorite) {
        super ();
        this.idacte = idacte;

        this.typeprestation = typeprestation;
        this.type_element = type_element;
        this.quantite = quantite;
        this.dateReception = dateReception;
        this.dateLivraison = dateLivraison;
        this.affectation = affectation;
        this.duree = duree;
        this.commentaire = commentaire;
        this.motif = motif;
        this.statutFacturation = statutFacturation;
        this.dateReprise = dateReprise;
        this.repriseFacturable = repriseFacturable;
        this.dateDeadline = dateDeadline;
        this.priorite = priorite;
    }


    public Acte_traitement(Prestation typeprestation, String type_element,
                           int quantite, Date dateReception, Date dateLivraison,
                           String affectation, int duree, String commentaire,
                           String motif, String statutFacturation, Date dateDeadline,  String priorite) {
        this.typeprestation = typeprestation;
        this.type_element = type_element;
        this.quantite = quantite;
        this.dateReception = dateReception;
        this.dateLivraison = dateLivraison;
        this.affectation = affectation;
        this.duree = duree;
        this.commentaire = commentaire;
        this.motif = motif;
        this.statutFacturation = statutFacturation;
        this.dateDeadline = dateDeadline;
        this.priorite = priorite;
    }

    //eya ajout, filtre
    public Acte_traitement(String idacte,Prestation prestation, String type_element,int quantite,
                           Date dateReception, Date dateLivraison,
                           String affectation, int duree, String commentaire,
                           String motif, String statutFacturation,  String priorite,String classification,
                           Date dateDeadline) {
        this.idacte = idacte;
        this.typeprestation = prestation;
        this.type_element = type_element;
        this.quantite=quantite;
        this.dateReception = dateReception;
        this.dateLivraison = dateLivraison;
        this.affectation = affectation;
        this.duree = duree;
        this.commentaire = commentaire;
        this.motif = motif;
        this.statutFacturation = statutFacturation;
        this.dateDeadline = dateDeadline;
        this.priorite = priorite;
        this.classification= classification;
    }
    public Acte_traitement(String motif, String typeElement, String commentaire,
                           Prestation pres, String statutFacturation, Date dateReception,
                           Date dateDeadline) {
        this.dateReception= dateReception;
        this.commentaire=commentaire;
        this.motif=motif;
        this.typeprestation=pres;
        this.type_element=typeElement;
        this.statutFacturation=statutFacturation;
        this.dateDeadline=dateDeadline;
    }
    public Acte_traitement(String motif, String typeElement, String commentaire,
                           Prestation pres, String statutFacturation, Date dateReception,
                           int quantite) {
        this.dateReception= dateReception;
        this.commentaire=commentaire;
        this.motif=motif;
        this.typeprestation=pres;
        this.type_element=typeElement;
        this.statutFacturation=statutFacturation;
        this.quantite=quantite;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}

