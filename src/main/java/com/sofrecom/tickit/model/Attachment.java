package com.sofrecom.tickit.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing a file attachment for a ticket activity.
 * Stores metadata about the file while the actual content is stored on disk.
 */
@Entity
@Table(name = "attachments",
    indexes = {
        @Index(name = "idx_attachment_activity", columnList = "activity_id")
    })
@Where(clause = "deleted = false")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attachment {

    /**
     * Unique identifier for the attachment.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Reference to the activity this attachment belongs to.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "activity_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_attachment_activity")
    )
    private TicketActivity activity;

    /**
     * Original filename provided by the user.
     */
    @Column(name = "filename", nullable = false)
    private String filename;

    /**
     * MIME type of the file (e.g., "image/jpeg", "application/pdf").
     */
    @Column(name = "content_type", nullable = false)
    private String contentType;

    /**
     * Size of the file in bytes.
     */
    @Column(name = "size", nullable = false)
    private Long size;

    /**
     * Relative path where the file is stored on disk or cloud storage.
     * This is not the full path, but a relative path from the storage root.
     */
    @Column(name = "storage_path", nullable = false, unique = true)
    private String storagePath;

    /**
     * Indicates whether this record has been soft-deleted.
     */
    @Column(nullable = false)
    private boolean deleted = false;

    /**
     * Timestamp when this record was soft-deleted.
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * Timestamp when this record was created.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when this record was last updated.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}