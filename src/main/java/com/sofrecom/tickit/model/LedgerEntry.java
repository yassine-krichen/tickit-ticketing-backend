package com.sofrecom.tickit.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents an immutable ledger entry in the blockchain ledger for the Treatment Act Management System.
 * <p>
 * Each entry records a snapshot of an entity's state (such as a treatment act or case) along with
 * critical metadata for ensuring integrity, security, and transparency. This includes cryptographic hashes,
 * a digital signature, and structured fields for efficient querying.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ledger_entry")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class LedgerEntry {

    /**
     * Unique identifier for this ledger entry.
     */
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    /**
     * Cryptographic hash of this ledger entry.
     * It is computed from the entry's content and helps to detect tampering.
     */
    @Column(name = "hash", nullable = false, columnDefinition = "text", length = 1000)
    private String hash;

    /**
     * Hash of the previous ledger entry in the chain.
     * For the very first (genesis) entry, this could be a constant value like "GENESIS" or "0".
     */
    @Column(name = "previous_hash", nullable = false, columnDefinition = "text", length = 1000)
    private String previousHash;

    /**
     * Timestamp when this ledger entry was created.
     */
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    /**
     * Identifier or fingerprint of the user or system that performed the action.
     * This is used for auditing and ensuring non-repudiation.
     */
    @Column(name = "user_fingerprint", nullable = false)
    private String userFingerprint;

    /**
     * The type of action recorded by this ledger entry.
     * Examples include CREATE, UPDATE, or DELETE.
     */
    @Column(name = "action_type", nullable = false)
    private ActionType actionType;

    /**
     * The type of entity associated with this ledger entry (e.g., "CASE" or "TREATMENT_ACT").
     * Having this as a structured field improves query performance.
     */
    @Column(name = "entity_type", nullable = false)
    private String entityType;

    /**
     * Unique identifier of the entity (case or treatment act) that this ledger entry records.
     * This field enables efficient filtering and indexing.
     */
    @Column(name = "entity_id", nullable = false)
    private String entityId;

    /**
     * Identifier of the case to which this ledger entry belongs.
     * This field is essential for tracking the progression of treatment acts within a case.
     */
    @Column(name = "case_id", nullable = false)
    private String caseId;

    /**
     * A JSONB snapshot of the entity's state at the time of this action.
     * <p>
     * The JSON snapshot (using Jackson's JsonNode) allows for flexible storage of the entity state,
     * and PostgreSQL's JSONB support enables you to perform queries on nested data when needed.
     * </p>
     */
    @Type(type = "jsonb")
    @Column(name = "data_snapshot", columnDefinition = "jsonb", nullable = false)
    private JsonNode dataSnapshot;


    /**
     * Digital signature of this ledger entry.
     * It is generated using a private key and ensures the authenticity and integrity of the entry.
     * An example value might be a Base64-encoded string.
     */
    @Column(name = "digital_signature", nullable = false, columnDefinition = "text", length = 1000)
    private String digitalSignature;

    /**
     * Enum representing the possible types of actions recorded in a ledger entry.
     * Using an enum ensures only valid action types can be set.
     */
    public enum ActionType {
        CREATE,
        UPDATE,
        DELETE
    }
}
