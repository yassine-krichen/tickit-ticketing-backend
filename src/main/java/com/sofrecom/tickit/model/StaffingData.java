package com.sofrecom.tickit.model;

public class StaffingData {
    private String month;
    private String typeTravail;
    private float count;
    private float formationCount;

    public StaffingData(String month, String typeTravail, float count, float formationCount) {
        this.month = month;
        this.typeTravail = typeTravail;
        this.count = count;
        this.formationCount = formationCount;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getTypeTravail() {
        return typeTravail;
    }

    public void setTypeTravail(String typeTravail) {
        this.typeTravail = typeTravail;
    }

    public float getCount() {
        return count;
    }

    public void setCount(float count) {
        this.count = count;
    }

    public float getFormationCount() {
        return formationCount;
    }

    public void setFormationCount(float formationCount) {
        this.formationCount = formationCount;
    }

    public StaffingData() {
    }
}
