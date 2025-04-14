package com.sofrecom.tickit.model;

public enum CaseType {

    GRAFIC("Grafic"),
    XOIP("XOIP"),
    TRAVAUX_TIERS("TravauxTiers"),
    MUTATION("Mutation"),
    PORTA_SORTANTE("PortaSortante"), // note: you may want to combine with "Porta Sortante"
    EVOLUTION_STATUT_IMB("Evolution statut IMB"),
    INTERCONNEXION_RIP_TIERS("Interconnexion RIP Tiers"),
    DOMMAGES("Dommages"),
    LIEN_NROPM("Lien-NROPM"),
    FACTU_TIERS("FactuTiers"),
    DESATURATIONS_COUPLEURS("DesaturationsCoupleurs"),
    // You might wish to address the duplicate here if "PortaSortante" and "Porta Sortante" are intended to be the same.
    FACTURATION_TECHNICIEN("FacturationTechnicien"),
    RECALAGE("Recalage"),
    RECALAGE_IA("RecalageIA"),
    MUTATION_NATNE("MUTATION NATNE"),
    MUTATION_NATHD("MUTATION NATHD"),
    MUTATION_NAT42C("MUTATION NAT42C"),
    INTERCONNEXION_RIP_TIERS_PHASE_1("Interconnexion RIP Tiers - Phase 1"),
    INTERCONNEXION_RIP_TIERS_PHASE_2("Interconnexion RIP Tiers - Phase 2"),
    INTERCONNEXION_RIP_TIERS_CONTROLE_TRAVAUX("Interconnexion RIP Tiers - Contrôle Travaux"),
    BRAME_PRE_ETUDE("BRAME - Pre-Etude"),
    BRAME_ETUDE("BRAME - Etude"),
    BRAME_RACCORDEMENT("BRAME - Raccordement"),
    BRAME_CONTROLE_TRAVAUX("BRAME - Contrôle Travaux"),
    BRAME("BRAME"),
    AFFECTATION_ET_MISE_EN_CAUSE("AFFECTATION_ET_MISE_EN_CAUSE"),
    CREANCE("CREANCE"),
    RAPPEL("RAPPEL"),
    MISE_EN_DEMEURE("MISE_EN_DEMEURE"),
    DERNIERE_LETTRE("DERNIERE_LETTRE"),
    MISE_CONTENTIEUX("MISE_CONTENTIEUX"),
    DEMANDE_DINFORMATIONS("DEMANDE_DINFORMATIONS"),
    CONTESTATION_OU_RECLAMATION("CONTESTATION_OU_RECLAMATION"),
    CLOTURE_DOSSIER("CLOTURE_DOSSIER"),
    CONTROLE_TRAVAUX_LIEN_NROPM("ControleTravaux - LienNropm"),
    CONTROLE_TRAVAUX_DESATURATIONS_COUPLEURS("ControleTravaux - DesaturationsCoupleurs"),
    CONTROLE_TRAVAUX("ControleTravaux"),
    MISSING_CUIVRE_FIBRE_CLASSIQUE("Missing Cuivre Fibre - Classique"),
    GEORESO_IMB("Georeso - IMB"),
    GEORESO_SITE_SUPPORT("Georeso - Site Support"),
    GEORESO_CABLE("Georeso - Cable"),
    GEORESO("Georeso"),
    BO_RIP("BO RIP"),
    BO_RIP_GC("BO RIP - GC"),
    BO_RIP_CABLAGE("BO RIP - Cablage"),
    BO_RIP_GC_CABLAGE("BO RIP - GC Cablage");

    private final String value;

    CaseType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
