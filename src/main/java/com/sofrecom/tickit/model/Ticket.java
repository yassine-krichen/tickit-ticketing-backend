package com.sofrecom.tickit.model;

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

@Entity
@Table(name = "ticket", indexes = {
        @Index(name = "idx_ticket_priority", columnList = "priority"),
        @Index(name = "idx_ticket_status", columnList = "status"),
        @Index(name = "idx_ticket_acte_traitement", columnList = "acte_traitement_id"),
        @Index(name = "idx_ticket_issued_by", columnList = "issued_by")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Ticket {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type = "uuid-char")
    @Column(name = "id", length = 36, updatable = false, nullable = false)
    private UUID id;

    @Column(name = "acte_traitement_id", nullable = false)
    private String acteTraitement;

    @Column(name = "subject", nullable = false, length = 255)
    private String subject;

    @Column(name = "description", length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 10)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private Status status;

    @Column(name = "issued_by", updatable = false, length = 50)
    private String issuedBy;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "response_sla")
    private Duration responseSla;

    @Column(name = "resolution_sla")
    private Duration resolutionSla;

    public enum Priority {
        P1, P2, P3, P4, P5
    }

    public enum Status {
        OPEN, IN_PROGRESS, BLOCKED, RESOLVED, CLOSED
    }
} 
