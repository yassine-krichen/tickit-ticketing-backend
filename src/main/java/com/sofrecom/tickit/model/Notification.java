package com.sofrecom.tickit.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Entity representing a notification in the system.
 * Notifications are tied to specific users and can target users with specific roles.
 */
@Entity
@Table(name = "notifications", indexes = {
    @Index(name = "idx_notification_user", columnList = "user_id"),
    @Index(name = "idx_notification_read_flag", columnList = "read_flag"),
    @Index(name = "idx_notification_type", columnList = "type"),
    @Index(name = "idx_notification_created_at", columnList = "created_at")
})
@TypeDefs({
    @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents a notification for users in the system")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier for the notification")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "User to whom this notification belongs")
    private User userRef;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "notification_target_roles",
        joinColumns = @JoinColumn(
            name = "notification_id",
            foreignKey = @ForeignKey(
                name = "fk_notification_role",
                foreignKeyDefinition = "FOREIGN KEY (notification_id) REFERENCES notifications(id) ON DELETE CASCADE"
            )
        )
    )
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    @Schema(description = "Target roles for this notification")
    private Set<ERole> target;

    @NotNull
    @Size(max = 500)
    @Schema(description = "Human-readable notification message", required = true)
    private String message;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Schema(description = "JSON payload with additional context for the notification")
    private JsonNode payload;

    @NotNull
    @Builder.Default
    @Column(name = "read_flag")
    @Schema(description = "Whether the notification has been read", required = true)
    private Boolean readFlag = false;

    @NotNull
    @Column(name = "created_at", nullable = false, updatable = false)
    @Schema(description = "When the notification was created", required = true)
    private LocalDateTime createdAt;

    @Column(name = "read_at")
    @Schema(description = "When the notification was marked as read")
    private LocalDateTime readAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Schema(description = "The type of notification", required = true)
    private NotificationType type;

    /**
     * Pre-persist hook to set creation timestamp.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}