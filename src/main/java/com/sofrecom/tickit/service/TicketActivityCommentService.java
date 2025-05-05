package com.sofrecom.tickit.service;

import com.sofrecom.tickit.dto.TicketActivityCommentRequest;
import com.sofrecom.tickit.dto.TicketActivityCommentResponse;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing ticket activity comments.
 * Provides methods for CRUD operations on comments associated with ticket activities.
 */
public interface TicketActivityCommentService {
    
    /**
     * Creates a new comment for a ticket activity.
     *
     * @param ticketId The ID of the ticket
     * @param activityId The ID of the activity
     * @param request The comment request details
     * @return The created comment response
     */
    TicketActivityCommentResponse createComment(UUID ticketId, Long activityId, TicketActivityCommentRequest request);
    
    /**
     * Gets all comments for a ticket activity.
     *
     * @param ticketId The ID of the ticket
     * @param activityId The ID of the activity
     * @return List of comment responses
     */
    List<TicketActivityCommentResponse> getCommentsByActivityId(UUID ticketId, Long activityId);
    
    /**
     * Gets a specific comment by ID.
     *
     * @param ticketId The ID of the ticket
     * @param activityId The ID of the activity
     * @param commentId The ID of the comment
     * @return The comment response
     */
    TicketActivityCommentResponse getCommentById(UUID ticketId, Long activityId, Long commentId);
    
    /**
     * Updates an existing comment.
     *
     * @param ticketId The ID of the ticket
     * @param activityId The ID of the activity
     * @param commentId The ID of the comment
     * @param request The updated comment details
     * @return The updated comment response
     */
    TicketActivityCommentResponse updateComment(UUID ticketId, Long activityId, Long commentId, TicketActivityCommentRequest request);
    
    /**
     * Deletes a comment (soft delete).
     *
     * @param ticketId The ID of the ticket
     * @param activityId The ID of the activity
     * @param commentId The ID of the comment
     */
    void deleteComment(UUID ticketId, Long activityId, Long commentId);
}