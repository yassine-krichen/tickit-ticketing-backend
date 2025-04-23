package com.sofrecom.tickit.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import javax.persistence.Transient;
import javax.persistence.PostLoad;

import java.time.Duration;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing activities performed on tickets.
 * Tracks user actions, effort time, and maintains audit information.
 */
@Entity
@Table(name = "ticket_activities",
indexes = {
  // use the real column name (start_time), not “timestamp”
  @Index(name = "idx_ticket_start_ts", columnList = "ticket_id, start_time DESC"),
  @Index(name = "idx_type_user",     columnList = "type, username")
})
@Where(clause = "deleted = false")    // Hibernate soft-delete filter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketActivity {
    
    /**
     * Unique identifier for the activity.
     */
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    /**
     * Reference to the ticket this activity belongs to.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "ticket_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_activity_ticket")
    )
    private Ticket ticket;

    /**
     * Username of the person who performed this activity.
     */
    @Column(nullable = false)
    private String username;

    /**
     * Type of activity performed.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityType type;

    /**
     * Detailed description of the activity performed.
     */
    @Column(length = 2000)
    private String description;

    /**
     * When the activity was started.
     */
    @Column(name = "start_time", nullable = false, updatable = false)
    private LocalDateTime startTime = LocalDateTime.now();

    /**
     * When the activity was completed (null if still in progress).
     */
    private LocalDateTime endTime;

    /**
     * Computed duration between start and end time.
     */
    @Transient
    private Duration duration;

    /**
     * Indicates whether this record has been soft-deleted.
     */
    @Column(nullable = false)
    private boolean deleted = false;

    /**
     * Timestamp when this record was soft-deleted.
     */
    private LocalDateTime deletedAt;

    /**
     * Timestamp when this record was created.
     */
    @CreationTimestamp
    private LocalDateTime createdAt;

    /**
     * Timestamp when this record was last updated.
     */
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /**
     * Computes the duration between start and end times when the entity is loaded or updated.
     */
    @PostLoad
    private void computeDuration() {
        if (endTime != null) {
            this.duration = Duration.between(startTime, endTime);
        }
    }

    /**
     * Types of activities that can be performed on a ticket.
     */
    public enum ActivityType {
        /**
         * When the ticket is created, we log this activity.
         */
        OPEN,
        
        /**
         * When the ticket is resolved and closed.
         */
        CLOSE,
        
        /**
         * When any information on the ticket is updated, such as status, context, or description.
         */
        UPDATE,
        
        /**
         * When the user sends an email related to the ticket.
         */
        EMAIL,
        
        /**
         * When the user makes a call related to the ticket.
         */
        CALL,
        
        /**
         * When the user performs research related to the ticket.
         */
        RESEARCH,
        
        /**
         * When the ticket is assigned to someone.
         */
        ASSIGNMENT,
        
        /**
         * For any other activity not covered by the predefined types.
         */
        OTHER
    }
}
