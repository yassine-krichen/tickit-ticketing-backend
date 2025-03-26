package com.sofrecom.tickit.model;

public class KpiData {
    private String month;
    private String prestation;
    private Group group;

    private double realise;
    private double staffing;
    private double ctj;
    private double dmtDeclare;
    private double etp;
    private double productivite;
    private double euroEtp;
    private double euroBpu;
    private double balance;
    private double formation;

    private double bpu;

    public KpiData() {
    }

    public KpiData(String month, String prestation, Group group, double realise, double staffing, double ctj, double dmtDeclare, double etp, double productivite, double euroEtp, double euroBpu, double balance, double formation, double bpu) {
        this.month = month;
        this.prestation = prestation;
        this.group = group;
        this.realise = realise;
        this.staffing = staffing;
        this.ctj = ctj;
        this.dmtDeclare = dmtDeclare;
        this.etp = etp;
        this.productivite = productivite;
        this.euroEtp = euroEtp;
        this.euroBpu = euroBpu;
        this.balance = balance;
        this.formation = formation;
        this.bpu = bpu;
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

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public double getRealise() {
        return realise;
    }

    public void setRealise(double realise) {
        this.realise = realise;
    }

    public double getStaffing() {
        return staffing;
    }

    public void setStaffing(double staffing) {
        this.staffing = staffing;
    }

    public double getCtj() {
        return ctj;
    }

    public void setCtj(double ctj) {
        this.ctj = ctj;
    }

    public double getDmtDeclare() {
        return dmtDeclare;
    }

    public void setDmtDeclare(double dmtDeclare) {
        this.dmtDeclare = dmtDeclare;
    }

    public double getEtp() {
        return etp;
    }

    public void setEtp(double etp) {
        this.etp = etp;
    }

    public double getProductivite() {
        return productivite;
    }

    public void setProductivite(double productivite) {
        this.productivite = productivite;
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

    public double getBpu() {
        return bpu;
    }

    public void setBpu(double bpu) {
        this.bpu = bpu;
    }
}