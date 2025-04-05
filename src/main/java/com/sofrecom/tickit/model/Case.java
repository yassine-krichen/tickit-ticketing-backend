package com.sofrecom.tickit.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * This class helps us track Treatment acts that are associated within the same case.
 * Case 1..1--> have -->1..* TreatmentAct
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cases")
public class Case {

    /**
     * Unique identifier for the case.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     * Descriptive name for the case.
     */
    private String name;

    /**
     * Detailed description providing context and additional information about the case.
     */
    private String description;

     /**
     * List of tags associated with the case for easier searchability.
     * Using @ElementCollection to persist the list of Strings.
     */
    @ElementCollection
    @CollectionTable(name = "case_tags", joinColumns = @JoinColumn(name = "case_id"))
    @Column(name = "tag")
    private List<String> tags;

    /**
     * Type of the case in our system (referred to as "prestation").
     */
    private String type;

    /**
     * Current status of the case.
     * Possible values: BLOCKED, ONGOING, CLOSED, CANCELED.
     */
    private Status status;

    /**
     * Priority level of the case.
     * Possible values: LOW, MEDIUM, HIGH.
     */
    private Priority priority;

    /**
     * Country associated with the case.
     * Possible values: TUNISIA, MOROCCO.
     */
    private Team team;

    /**
     * Timestamp when the case was created.
     */
    private LocalDateTime createdAt;

    /**
     * Identifier of the user who created the case.
     */
    private String createdBy;

    /**
     * Timestamp when the case was last updated.
     */
    private LocalDateTime updatedAt;

    /**
     * List of treatment acts associated with this case.
     */
    @OneToMany(mappedBy = "associatedCase", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Acte_traitement> treatmentActs;

    /**
     * Enum representing the possible statuses of a treatment case.
     */
    public enum Status {
        BLOCKED,
        ONGOING,
        CLOSED,
        CANCELED
    }

    /**
     * Enum representing the priority levels for a treatment case.
     */
    public enum Priority {
        LOW,
        MEDIUM,
        HIGH
    }

    /**
     * Enum representing the countries applicable to a treatment case.
     */
    public enum Team {
        TUNISIA,
        MOROCCO
    }
}
