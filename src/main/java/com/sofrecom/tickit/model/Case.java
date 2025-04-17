package com.sofrecom.tickit.model;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Represents a case that groups related treatment acts")
public class Case {

    /**
     * Unique identifier for the case.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Schema(description = "Unique identifier for the case", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    /**
     * Descriptive name for the case.
     */
    @Schema(description = "Descriptive name for the case", example = "Customer Inquiry - Orange ADSL")
    private String name;

    /**
     * Detailed description providing context and additional information about the case.
     */
    @Schema(description = "Detailed description of the case", example = "Case opened following multiple customer reports of ADSL connectivity issues in the Tunis region.")
    private String description;

     /**
     * List of tags associated with the case for easier searchability.
     * Using @ElementCollection to persist the list of Strings.
     */
    @ElementCollection
    @CollectionTable(name = "case_tags", joinColumns = @JoinColumn(name = "case_id"))
    @Column(name = "tag")
    @Schema(description = "List of tags for categorization and searchability", example = "[\"network\", \"connectivity\", \"ADSL\"]")
    private List<String> tags;

    /**
     * Type of the case in our system (referred to as "prestation").
     */
    @Enumerated(EnumType.STRING)
    @Schema(description = "Type of the case in the system", implementation = CaseType.class)
    private CaseType type;

    /**
     * Current status of the case.
     * Possible values: BLOCKED, ONGOING, CLOSED, CANCELED.
     */
    @Schema(description = "Current status of the case", example = "ONGOING", implementation = Status.class)
    private Status status;

    /**
     * Priority level of the case.
     * Possible values: LOW, MEDIUM, HIGH.
     */
    @Schema(description = "Priority level of the case", example = "HIGH", implementation = Priority.class)
    private Priority priority;

    /**
     * Country associated with the case.
     * Possible values: TUNISIA, MOROCCO.
     */
    @Schema(description = "Team or country associated with the case", example = "TUNISIA", implementation = Team.class)
    private Team team;

    /**
     * Timestamp when the case was created.
     */
    @Schema(description = "Timestamp when the case was created", example = "2023-01-10T09:30:00")
    private LocalDateTime createdAt;

    /**
     * Identifier of the user who created the case.
     */
    @Schema(description = "Username or ID of the person who created the case", example = "john.doe")
    private String createdBy;

    /**
     * Timestamp when the case was last updated.
     */
    @Schema(description = "Timestamp when the case was last updated", example = "2023-01-15T14:20:00")
    private LocalDateTime updatedAt;

    /**
     * List of treatment acts associated with this case.
     */
    @OneToMany(mappedBy = "associatedCase", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Schema(description = "List of treatment acts associated with this case")
    private List<Acte_traitement> treatmentActs;

    /**
     * Enum representing the possible statuses of a treatment case.
     */
    @Schema(description = "Possible statuses for a case in its lifecycle")
    public enum Status {
        BLOCKED,
        ONGOING,
        CLOSED,
        CANCELED
    }

    /**
     * Enum representing the priority levels for a treatment case.
     */
    @Schema(description = "Priority levels for cases from lowest to highest")
    public enum Priority {
        LOW,
        MEDIUM,
        HIGH
    }

    /**
     * Enum representing the countries applicable to a treatment case.
     */
    @Schema(description = "Teams or countries associated with cases")
    public enum Team {
        TUNISIA,
        MOROCCO
    }
}
