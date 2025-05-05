package com.sofrecom.tickit.impl;

import com.sofrecom.tickit.config.StorageProperties;
import com.sofrecom.tickit.exception.FileStorageException;
import com.sofrecom.tickit.exception.ResourceNotFoundException;
import com.sofrecom.tickit.model.Attachment;
import com.sofrecom.tickit.model.TicketActivity;
import com.sofrecom.tickit.repository.AttachmentRepository;
import com.sofrecom.tickit.repository.TicketActivityRepository;
import com.sofrecom.tickit.service.AttachmentService;
import com.sofrecom.tickit.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Implementation of AttachmentService.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final TicketActivityRepository activityRepository;
    private final FileStorageService fileStorageService;
    private final StorageProperties storageProperties;

    /**
     * Validates a file before storing it.
     *
     * @param file The file to validate
     */
    private void validateFile(MultipartFile file) {
        // Check file size
        if (file.getSize() > storageProperties.getMaxFileSize()) {
            throw new FileStorageException("File exceeds maximum size of " + 
                    (storageProperties.getMaxFileSize() / (1024 * 1024)) + "MB");
        }
        
        // Check content type
        String contentType = file.getContentType();
        if (contentType == null || !Arrays.asList(storageProperties.getAllowedTypes()).contains(contentType)) {
            throw new FileStorageException("Unsupported file type: " + contentType);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Attachment createAttachment(Long activityId, MultipartFile file) throws IOException {
        // Validate file
        validateFile(file);
        
        // Get activity
        TicketActivity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("Activity", "ID", activityId));
        
        // Store file
        String storagePath = fileStorageService.store(file, activityId);
        
        // Create attachment entity
        Attachment attachment = Attachment.builder()
                .activity(activity)
                .filename(file.getOriginalFilename())
                .contentType(file.getContentType())
                .size(file.getSize())
                .storagePath(storagePath)
                .build();
        
        // Save attachment
        return attachmentRepository.save(attachment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Attachment> getAttachmentsByActivityId(Long activityId) {
        if (!activityRepository.existsById(activityId)) {
            throw new ResourceNotFoundException("Activity", "ID", activityId);
        }
        
        return attachmentRepository.findByActivity_Id(activityId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Attachment getAttachmentById(Long activityId, Long attachmentId) {
        return attachmentRepository.findByIdAndActivity_Id(attachmentId, activityId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment", "ID", attachmentId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Resource loadAttachmentAsResource(Long activityId, Long attachmentId) {
        Attachment attachment = getAttachmentById(activityId, attachmentId);
        
        try {
            Path filePath = fileStorageService.load(attachment.getStoragePath());
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new FileStorageException("Could not read file: " + attachment.getFilename());
            }
        } catch (MalformedURLException e) {
            throw new FileStorageException("Malformed URL for file: " + attachment.getFilename(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteAttachment(Long activityId, Long attachmentId) {
        Attachment attachment = getAttachmentById(activityId, attachmentId);
        
        // Soft delete
        attachment.setDeleted(true);
        attachment.setDeletedAt(LocalDateTime.now());
        
        attachmentRepository.save(attachment);
        
        log.info("Soft-deleted attachment: {}", attachment.getId());
    }
}