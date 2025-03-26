package com.sofrecom.tickit.model.enumList;

public enum TypeTraitement {
    AFFECTATION_ET_MISE_EN_CAUSE(1),
    CREANCE(2),
    RAPPEL(3),
    MISE_EN_DEMEURE(4),
    DERNIERE_LETTRE(5),
    MISE_CONTENTIEUX(6),
    DEMANDE_DINFORMATIONS(7),
    CONTESTATION_OU_RECLAMATION(8),
    CLOTURE_DOSSIER(9);

    private final int step;

    TypeTraitement(int step) {
        this.step = step;
    }

    public int getStep() {
        return step;
    }
}