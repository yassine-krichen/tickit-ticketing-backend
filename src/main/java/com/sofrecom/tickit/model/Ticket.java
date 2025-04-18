package com.sofrecom.tickit.model;

import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.FetchType;

/**
 * Represents a support ticket in the system.
 */
@Entity
@Table(name = "ticket", indexes = {
        @Index(name = "idx_ticket_priority", columnList = "priority"),
        @Index(name = "idx_ticket_status", columnList = "status"),
        @Index(name = "idx_ticket_acte_traitement", columnList = "acte_traitement_id"),
        @Index(name = "idx_ticket_issued_by", columnList = "issued_by"),
        @Index(name = "idx_ticket_type", columnList = "type"),
        @Index(name = "idx_ticket_step", columnList = "step"),
        @Index(name = "idx_ticket_sub_status", columnList = "sub_status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "Represents a ticket in the ticketing system")
public class Ticket {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type = "uuid-char")
    @Column(name = "id", length = 36, updatable = false, nullable = false)
    @Schema(description = "Unique identifier of the ticket", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acte_traitement_id", nullable = false)
    @Schema(description = "Treatment act associated with this ticket")
    private Acte_traitement acteTraitement;

    @Column(name = "subject", nullable = false, length = 255)
    @Schema(description = "Subject or title of the ticket", example = "System not responding")
    private String subject;

    @Column(name = "description", length = 2000)
    @Schema(description = "Detailed description of the ticket issue", example = "When trying to access the dashboard, the system returns a 500 error.")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 50)
    @Schema(description = "Type of treatment act associated with this ticket", example = "GRAFIC")
    private CaseType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "step", length = 50)
    @Schema(description = "Current step or phase of the treatment act", example = "BRAME")
    private CaseType step;

    @Column(name = "sub_status", length = 50)
    @Schema(description = "Sub-status providing additional context about the ticket's state", example = "PENDING_APPROVAL")
    private String subStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 10)
    @Schema(description = "Priority level of the ticket", example = "P1", implementation = Priority.class)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Schema(description = "Current status of the ticket", example = "OPEN", implementation = Status.class)
    private Status status;

    @Column(name = "issued_by", updatable = false, length = 50)
    @Schema(description = "Username or ID of the person who created the ticket", example = "john.doe")
    private String issuedBy;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    @Schema(description = "Timestamp when the ticket was created", example = "2023-01-15T12:30:00")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    @Schema(description = "Timestamp when the ticket was last updated", example = "2023-01-16T09:45:00")
    private LocalDateTime updatedAt;

    @Column(name = "response_sla")
    @Schema(description = "Service Level Agreement duration for initial response", example = "PT4H")
    private Duration responseSla;

    @Column(name = "resolution_sla")
    @Schema(description = "Service Level Agreement duration for final resolution", example = "PT24H")
    private Duration resolutionSla;

    @Schema(description = "Priority levels for tickets, from highest (P1) to lowest (P5)")
    public enum Priority {
        P1, P2, P3, P4, P5
    }

    @Schema(description = "Possible statuses of a ticket in its lifecycle")
    public enum Status {
        OPEN, IN_PROGRESS, BLOCKED, RESOLVED, CLOSED
    }
} 
