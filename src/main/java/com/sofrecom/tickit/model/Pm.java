package com.sofrecom.tickit.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

@Entity
public class Pm {

@Id
@GeneratedValue
Integer idPm;

String refPm;


String idActPm;

@JsonBackReference
@ManyToOne
@JoinColumn(name="nropm_id")
Nropm nropm;

public Pm(){

}

public Pm(String refPm, String idActPm, Nropm nropm) {
this.refPm = refPm;
this.idActPm = idActPm;
this.nropm = nropm;
}

public Integer getIdPm() {
return idPm;
}

public void setIdPm(Integer idPM) {
this.idPm = idPM;
}

public String getRefPm() {
return refPm;
}

public void setRefPm(String refPm) {
this.refPm = refPm;
}

public String getIdActPm() {
return idActPm;
}

public void setIdActPm(String idActPm) {
this.idActPm = idActPm;
}

public Nropm getNropm() {
return nropm;
}

public void setNropm(Nropm nropm) {
this.nropm = nropm;
}
}