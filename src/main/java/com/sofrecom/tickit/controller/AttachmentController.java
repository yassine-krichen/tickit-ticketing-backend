package com.sofrecom.tickit.controller;

import com.sofrecom.tickit.dto.AttachmentResponse;
import com.sofrecom.tickit.exception.FileStorageException;
import com.sofrecom.tickit.mapper.AttachmentMapper;
import com.sofrecom.tickit.model.Attachment;
import com.sofrecom.tickit.service.AttachmentService;
import com.sofrecom.tickit.util.AuthUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for managing attachments on ticket activities.
 */
@RestController
@RequestMapping("/api/v1/activities/{activityId}/attachments")
@Tag(name = "Attachments", description = "Operations for managing attachments on ticket activities")
@RequiredArgsConstructor
@Slf4j
public class AttachmentController {

    private final AttachmentService attachmentService;
    private final AttachmentMapper attachmentMapper;
    private final AuthUtils authUtils;

    /**
     * Uploads one or more files as attachments to an activity.
     *
     * @param activityId The ID of the activity
     * @param files The files to upload
     * @return A response entity with the created attachments
     */
    @Operation(summary = "Upload attachments", 
              description = "Uploads one or more files as attachments to a ticket activity")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", 
                   description = "Attachments uploaded successfully", 
                   content = @Content(array = @ArraySchema(schema = @Schema(implementation = AttachmentResponse.class)))),
        @ApiResponse(responseCode = "400", 
                   description = "Invalid file type or size"),
        @ApiResponse(responseCode = "404", 
                   description = "Activity not found")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<AttachmentResponse>> uploadAttachments(
            @Parameter(description = "Unique identifier of the activity", required = true)
            @PathVariable Long activityId,
            @Parameter(description = "Files to upload", required = true)
            @RequestParam("files") MultipartFile[] files) {
        
        if (files.length == 0) {
            return ResponseEntity.badRequest().build();
        }
        
        List<AttachmentResponse> responses = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        
        // Verify that the authenticated user has permission to access this activity
        // This would typically involve checking if the user is associated with the activity
        // For now, we're just using authentication to track the user
        String username = authUtils.getAuthenticatedUsername();
        log.info("User {} uploading {} attachments to activity {}", username, files.length, activityId);
        
        for (MultipartFile file : files) {
            try {
                Attachment attachment = attachmentService.createAttachment(activityId, file);
                responses.add(attachmentMapper.toResponse(attachment));
            } catch (IOException | FileStorageException e) {
                errors.add(String.format("Error processing file '%s': %s", 
                        file.getOriginalFilename(), e.getMessage()));
                log.error("Error uploading file", e);
            }
        }
        
        if (responses.isEmpty() && !errors.isEmpty()) {
            // All files failed to upload
            return ResponseEntity.badRequest().body(null);
        }
        
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    /**
     * Gets all attachments for an activity.
     *
     * @param activityId The ID of the activity
     * @return A response entity with the list of attachments
     */
    @Operation(summary = "Get all attachments", 
              description = "Retrieves all attachments for a specific ticket activity")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                   description = "Attachments retrieved successfully", 
                   content = @Content(array = @ArraySchema(schema = @Schema(implementation = AttachmentResponse.class)))),
        @ApiResponse(responseCode = "404", 
                   description = "Activity not found")
    })
    @GetMapping
    public ResponseEntity<List<AttachmentResponse>> getAllAttachments(
            @Parameter(description = "Unique identifier of the activity", required = true)
            @PathVariable Long activityId) {
        
        List<Attachment> attachments = attachmentService.getAttachmentsByActivityId(activityId);
        List<AttachmentResponse> responses = attachmentMapper.toResponseList(attachments);
        
        return ResponseEntity.ok(responses);
    }

    /**
     * Downloads a specific attachment.
     *
     * @param activityId The ID of the activity
     * @param attachmentId The ID of the attachment
     * @return A response entity with the attachment file
     */
    @Operation(summary = "Download attachment", 
              description = "Downloads a specific attachment file")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                   description = "Attachment downloaded successfully"),
        @ApiResponse(responseCode = "404", 
                   description = "Attachment or activity not found")
    })
    @GetMapping("/{attachmentId}")
    public ResponseEntity<Resource> downloadAttachment(
            @Parameter(description = "Unique identifier of the activity", required = true)
            @PathVariable Long activityId,
            @Parameter(description = "Unique identifier of the attachment", required = true)
            @PathVariable Long attachmentId) {
        
        // Get attachment metadata
        Attachment attachment = attachmentService.getAttachmentById(activityId, attachmentId);
        
        // Load the file as a resource
        Resource resource = attachmentService.loadAttachmentAsResource(activityId, attachmentId);
        
        // Determine content type
        String contentType = attachment.getContentType();
        
        // Set response headers
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"" + attachment.getFilename() + "\"")
                .body(resource);
    }

    /**
     * Deletes a specific attachment.
     *
     * @param activityId The ID of the activity
     * @param attachmentId The ID of the attachment
     * @return A response entity with no content
     */
    @Operation(summary = "Delete attachment", 
              description = "Soft-deletes an attachment from a ticket activity")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", 
                   description = "Attachment deleted successfully"),
        @ApiResponse(responseCode = "404", 
                   description = "Attachment or activity not found")
    })
    @DeleteMapping("/{attachmentId}")
    public ResponseEntity<Void> deleteAttachment(
            @Parameter(description = "Unique identifier of the activity", required = true)
            @PathVariable Long activityId,
            @Parameter(description = "Unique identifier of the attachment", required = true)
            @PathVariable Long attachmentId) {
        
        attachmentService.deleteAttachment(activityId, attachmentId);
        return ResponseEntity.noContent().build();
    }
}