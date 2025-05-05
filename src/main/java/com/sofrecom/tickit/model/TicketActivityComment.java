package com.sofrecom.tickit.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Index;

import java.time.LocalDateTime;

import org.hibernate.annotations.Where;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing comments on ticket activities.
 * Allows tracking discussions related to specific activities.
 */
@Entity
@Table(name = "ticket_activity_comments",
    indexes = {
        @Index(name = "idx_activity_comment_ts", columnList = "activity_id, created_at DESC")
    })
@Where(clause = "deleted = false")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketActivityComment {
    
    /**
     * Unique identifier for the comment.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Reference to the activity this comment belongs to.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "activity_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_comment_activity")
    )
    private TicketActivity activity;
    
    /**
     * Content of the comment.
     */
    @Column(name = "content", nullable = false, length = 4000)
    private String content;
    
    /**
     * Username of the person who wrote this comment.
     */
    @Column(name = "username", nullable = false)
    private String username;
    
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
     * Timestamp when this comment was created.
     * Using manual lifecycle hooks instead of @CreationTimestamp to ensure better control.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Timestamp when this comment was last updated.
     * Will be null for newly created comments and set only on updates.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * Pre-persist lifecycle hook that sets the creation timestamp 
     * when the entity is first persisted.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        // Explicitly set updatedAt to null on creation
        this.updatedAt = null;
    }
    
    /**
     * Pre-update lifecycle hook that sets the update timestamp
     * when the entity is updated but preserves the creation timestamp.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        // We don't need to touch createdAt here as it's marked as updatable=false
    }
}