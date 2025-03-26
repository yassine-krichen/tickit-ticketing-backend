package com.sofrecom.tickit.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class BalanceData implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long BalanceDataId;
    private String month;
    private String prestation;
    @Column(name = "groupName")
    private String group;
    private double realise;
    private double euroEtp;
    private double euroBpu;
    private double balance;
    private double formation;
    private double objectifs;
    private double tarif;

    public BalanceData() {
    }

    public BalanceData(String month, String prestation, String group, double realise, double euroEtp, double euroBpu, double balance, double formation, double objectifs, double tarif) {
        this.month = month;
        this.prestation = prestation;
        this.group = group;
        this.realise = realise;
        this.euroEtp = euroEtp;
        this.euroBpu = euroBpu;
        this.balance = balance;
        this.formation = formation;
        this.objectifs = objectifs;
        this.tarif = tarif;
    }

    public Long getBalanceDataId() {
        return BalanceDataId;
    }

    public void setBalanceDataId(Long balanceDataId) {
        BalanceDataId = balanceDataId;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getPrestation() {
        return prestation;
    }

    public void setPrestation(String prestation) {
        this.prestation = prestation;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public double getRealise() {
        return realise;
    }

    public void setRealise(double realise) {
        this.realise = realise;
    }

    public double getEuroEtp() {
        return euroEtp;
    }

    public void setEuroEtp(double euroEtp) {
        this.euroEtp = euroEtp;
    }

    public double getEuroBpu() {
        return euroBpu;
    }

    public void setEuroBpu(double euroBpu) {
        this.euroBpu = euroBpu;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getFormation() {
        return formation;
    }

    public void setFormation(double formation) {
        this.formation = formation;
    }

    public double getObjectifs() {
        return objectifs;
    }

    public void setObjectifs(double objectifs) {
        this.objectifs = objectifs;
    }

    public double getTarif() {
        return tarif;
    }

    public void setTarif(double tarif) {
        this.tarif = tarif;
    }
}
