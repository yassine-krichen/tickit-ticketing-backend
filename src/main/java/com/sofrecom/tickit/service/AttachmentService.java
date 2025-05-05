package com.sofrecom.tickit.service;

import com.sofrecom.tickit.model.Attachment;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Service interface for attachment operations.
 */
public interface AttachmentService {

    /**
     * Creates a new attachment for a ticket activity.
     *
     * @param activityId The ID of the activity
     * @param file The file to attach
     * @return The created attachment
     * @throws IOException If an I/O error occurs
     */
    Attachment createAttachment(Long activityId, MultipartFile file) throws IOException;

    /**
     * Gets all attachments for a ticket activity.
     *
     * @param activityId The ID of the activity
     * @return List of attachments
     */
    List<Attachment> getAttachmentsByActivityId(Long activityId);

    /**
     * Gets a specific attachment by ID.
     *
     * @param activityId The ID of the activity
     * @param attachmentId The ID of the attachment
     * @return The attachment
     */
    Attachment getAttachmentById(Long activityId, Long attachmentId);

    /**
     * Loads an attachment as a Resource for streaming.
     *
     * @param activityId The ID of the activity
     * @param attachmentId The ID of the attachment
     * @return The attachment as a Resource
     */
    Resource loadAttachmentAsResource(Long activityId, Long attachmentId);

    /**
     * Deletes an attachment (soft delete).
     *
     * @param activityId The ID of the activity
     * @param attachmentId The ID of the attachment
     */
    void deleteAttachment(Long activityId, Long attachmentId);
}