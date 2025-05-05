package com.sofrecom.tickit.repository;

import com.sofrecom.tickit.model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Attachment entities.
 */
@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    /**
     * Finds all attachments for a specific activity.
     *
     * @param activityId The ID of the activity
     * @return List of attachments
     */
    List<Attachment> findByActivity_Id(Long activityId);
    
    /**
     * Finds a specific attachment that belongs to a specific activity.
     *
     * @param id The attachment ID
     * @param activityId The activity ID
     * @return The attachment if found
     */
    Optional<Attachment> findByIdAndActivity_Id(Long id, Long activityId);
}