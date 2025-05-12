package com.sofrecom.tickit.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.sofrecom.tickit.dto.NotificationFilterRequest;
import com.sofrecom.tickit.dto.NotificationResponse;
import com.sofrecom.tickit.model.ERole;
import com.sofrecom.tickit.model.NotificationType;

import java.util.List;
import java.util.Set;

/**
 * Service interface for notification operations.
 * Provides methods to create, retrieve, update, and delete notifications.
 */
public interface NotificationService {
    
    /**
     * Creates a notification for a specific user.
     *
     * @param userId The ID of the user to notify
     * @param type The type of notification
     * @param message The notification message
     * @param payload Additional data for the notification
     * @return The created notification response
     */
    NotificationResponse createForUser(Long userId, NotificationType type, String message, JsonNode payload);
    
    /**
     * Creates notifications for all users with specific roles.
     *
     * @param roles The target roles
     * @param type The type of notification
     * @param message The notification message
     * @param payload Additional data for the notification
     * @return The number of notifications created
     */
    int createForRoles(Set<ERole> roles, NotificationType type, String message, JsonNode payload);
    
    /**
     * Retrieves notifications for the current user with optional filtering.
     *
     * @param filter Filter criteria including read status, type, etc.
     * @param page Page number (0-based)
     * @param size Page size
     * @return List of notification responses matching the criteria
     */
    List<NotificationResponse> getAll(NotificationFilterRequest filter, int page, int size);
    
    /**
     * Retrieves a specific notification by ID.
     *
     * @param id The notification ID
     * @return The notification response
     */
    NotificationResponse getById(Long id);
    
    /**
     * Marks multiple notifications as read.
     *
     * @param ids The IDs of notifications to mark as read
     * @return The number of notifications updated
     */
    int setReadBulk(List<Long> ids);
    
    /**
     * Deletes multiple notifications.
     *
     * @param ids The IDs of notifications to delete
     * @return The number of notifications deleted
     */
    int deleteBulk(List<Long> ids);
    
    /**
     * Counts unread notifications for the current user.
     *
     * @return The number of unread notifications
     */
    long countUnread();
}