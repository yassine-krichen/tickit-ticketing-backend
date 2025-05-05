package com.sofrecom.tickit.repository;

import com.sofrecom.tickit.model.TicketActivityComment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for ticket activity comments.
 * Provides methods for database operations on TicketActivityComment entities.
 */
@Repository
public interface TicketActivityCommentRepository extends JpaRepository<TicketActivityComment, Long> {
    
    /**
     * Finds all comments for a specific activity ordered by creation time (newest first).
     *
     * @param activityId The ID of the activity
     * @return List of comments
     */
    List<TicketActivityComment> findByActivityIdOrderByCreatedAtDesc(Long activityId);
}