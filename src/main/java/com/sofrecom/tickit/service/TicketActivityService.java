package com.sofrecom.tickit.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sofrecom.tickit.dto.TicketActivityRequest;
import com.sofrecom.tickit.dto.TicketActivityResponse;
import com.sofrecom.tickit.model.TicketActivity.ActivityType;

public interface TicketActivityService {
    
    /**
     * Create and start a new activity for a ticket.
     */
    TicketActivityResponse createActivity(UUID ticketId, TicketActivityRequest request);

    /**
     * Update an existing activity (e.g. description or type).
     */
    TicketActivityResponse updateActivity(UUID ticketId, Long activityId, TicketActivityRequest request);

    /**
     * Mark an activity as completed, setting its endTime and computing duration.
     */
    TicketActivityResponse completeActivity(UUID ticketId, Long activityId);

    /**
     * Soft-delete an activity.
     */
    void deleteActivity(UUID ticketId, Long activityId);

    /**
     * Get paginated timeline for a ticket with advanced filtering and sorting.
     *
     * @param ticketId  UUID of the ticket
     * @param q         Optional search query for description
     * @param type      Optional filter by activity type
     * @param username  Optional filter by username
     * @param page      Page number (0-indexed)
     * @param size      Page size
     * @param sortField Field to sort by (startTime, createdAt, type, username)
     * @param sortDir   Sort direction (asc, desc)
     * @return Paginated list of TicketActivityResponse objects
     * @throws com.sofrecom.tickit.exception.InvalidSortParameterException if sortField or sortDir is invalid
     */
    Page<TicketActivityResponse> getTimeline(
            UUID ticketId,
            String q,
            ActivityType type,
            String username,
            int page,
            int size,
            String sortField,
            String sortDir);

    /**
     * Get a specific activity by its ID.
     *
     * @param ticketId UUID of the ticket
     * @param activityId ID of the activity
     * @return The activity response
     * @throws ResourceNotFoundException if activity not found
     */
    TicketActivityResponse getActivityById(UUID ticketId, Long activityId);
}