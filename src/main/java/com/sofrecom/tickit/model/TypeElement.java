package com.sofrecom.tickit.model;

import javax.persistence.*;

@Entity
public class TypeElement {

    @Id
    @GeneratedValue
    Integer idTypeElemnt;

    private String nomType;

    @ManyToOne
    @JoinColumn(name="prestation_id")
    Prestation typePrestation;


    public TypeElement(){
    }


    public TypeElement(String nomType, Prestation typePrestation) {
        this.nomType = nomType;
        this.typePrestation = typePrestation;
    }


    public Integer getIdTypeElemnt() {
        return idTypeElemnt;
    }

    public void setIdTypeElemnt(Integer idTypeElemnt) {
        this.idTypeElemnt = idTypeElemnt;
    }

    public String getNomType() {
        return nomType;
    }

    public void setNomType(String nomType) {
        this.nomType = nomType;
    }

    public Prestation getTypePrestation() {
        return typePrestation;
    }

    public void setTypePrestation(Prestation typePrestation) {
        this.typePrestation = typePrestation;
    }
}
