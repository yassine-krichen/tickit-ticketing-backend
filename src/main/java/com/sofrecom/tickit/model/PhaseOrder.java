package com.sofrecom.tickit.model;

import com.sofrecom.tickit.model.enumList.TypePhase;

import java.util.Arrays;
import java.util.List;

public class PhaseOrder {

    public static final List<TypePhase> PHASE_TYPES_ORDRE = Arrays.asList(
            TypePhase.AFFECTATION_ET_MISE_EN_CAUSE,
            TypePhase.CREANCE,
            TypePhase.RAPPEL_SIMPLE,
            TypePhase.MISE_EN_DEMEURE,
            TypePhase.MISE_CONTENTIEUX,
            TypePhase.CLOTURE_DOSSIER
    );

    public static String getNextPhaseAsString(String currentPhaseString) {
        if (TypePhase.RAPPEL_ASSURANCE.toString ().equals(currentPhaseString)) {
            return TypePhase.MISE_EN_DEMEURE.toString ();
        }
        if (TypePhase.MISE_EN_DEMEURE_ASSURANCE.toString ().equals(currentPhaseString)) {
            return TypePhase.MISE_CONTENTIEUX.toString ();
        }

        TypePhase currentPhase;
        try {
            currentPhase = TypePhase.valueOf(currentPhaseString);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid phase string: " + currentPhaseString);
            return null;
        }

        int currentIndex = PHASE_TYPES_ORDRE.indexOf(currentPhase);
        if (currentIndex != -1 && currentIndex < PHASE_TYPES_ORDRE.size() - 1) {
            return PHASE_TYPES_ORDRE.get(currentIndex + 1).name();
        }
        return null;
    }

    public static String getLastPhaseFrom(String currentPhaseString) {
        TypePhase currentPhase;
        try {
            currentPhase = TypePhase.valueOf(currentPhaseString);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid phase string: " + currentPhaseString);
            return null;
        }

        int currentIndex = PHASE_TYPES_ORDRE.indexOf(currentPhase);
        if (currentIndex > 0) {
            return PHASE_TYPES_ORDRE.get(currentIndex - 1).name();
        }
        return null; // Return null if it's the first phase or not found
    }



}
