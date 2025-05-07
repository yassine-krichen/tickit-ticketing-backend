package com.sofrecom.tickit.model;

/**
 * Enum representing the various types of notifications in the system.
 * Each type serves a specific purpose and may require different handling.
 */
public enum NotificationType {
    /**
     * When a new ticket is created.
     */
    TICKET_CREATED,

    /**
     * When a ticket is resolved.
     */
    TICKET_RESOLVED,

    /**
     * When a ticket's priority is escalated.
     */
    PRIO_LEVELED,

    /**
     * When an activity has stalled.
     */
    ACTIVITY_STALLED,

    /**
     * When a case has been blocked.
     */
    CASE_BLOCKED,

    /**
     * When a due date is approaching.
     */
    DUE_DATE_APPROACHING,

    /**
     * When an SLA breach occurs.
     */
    SLA_BREACH,

    /**
     * General system announcements.
     */
    SYSTEM_ANNOUNCEMENT,

    /**
     * When a ticket (including its activities) is updated.
     */
    UPDATE
}