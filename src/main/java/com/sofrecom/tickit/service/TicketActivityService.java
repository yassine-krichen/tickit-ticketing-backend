package com.sofrecom.tickit.service;

import java.util.UUID;

import org.springframework.data.domain.Page;

import com.sofrecom.tickit.dto.TicketActivityRequest;
import com.sofrecom.tickit.dto.TicketActivityResponse;
import com.sofrecom.tickit.model.TicketActivity.ActivityType;
import org.springframework.data.domain.Pageable;

public interface TicketActivityService {
    
/**
     * Create and start a new activity for a ticket.
     */
    TicketActivityResponse createActivity(UUID ticketId,
                                           TicketActivityRequest request);

    /**
     * Update an existing activity (e.g. description or type).
     */
    TicketActivityResponse updateActivity(UUID ticketId,
                                           Long activityId,
                                           TicketActivityRequest request);

    /**
     * Mark an activity as completed, setting its endTime and computing duration.
     */
    TicketActivityResponse completeActivity(UUID ticketId,
                                             Long activityId);

    /**
     * Soft-delete an activity.
     */
    void deleteActivity(UUID ticketId,
                        Long activityId);

    /**
     * Get paginated timeline for a ticket, optionally filtered by type or username.
     */
    Page<TicketActivityResponse> getTimeline(UUID ticketId,
                                              ActivityType type,
                                              String username,
                                              Pageable pageable);
}