package com.sofrecom.tickit.controller;

import com.sofrecom.tickit.dto.TicketActivityCommentRequest;
import com.sofrecom.tickit.dto.TicketActivityCommentResponse;
import com.sofrecom.tickit.service.TicketActivityCommentService;
import com.sofrecom.tickit.util.AuthUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing comments on ticket activities.
 * Provides endpoints for creating, retrieving, updating, and deleting comments.
 */
@RestController
@RequestMapping("/api/v1/tickets/{ticketId}/activities/{activityId}/comments")
@Tag(name = "Activity Comments", description = "Operations for managing comments on ticket activities")
@Validated
@RequiredArgsConstructor
public class TicketActivityCommentController {

    private final TicketActivityCommentService commentService;
    private final AuthUtils authUtils;

    /**
     * Creates a new comment on a ticket activity.
     *
     * @param ticketId The ID of the ticket
     * @param activityId The ID of the activity
     * @param request The comment request containing details
     * @return A response entity with the created comment
     */
    @Operation(summary = "Create a new comment", 
              description = "Adds a new comment to a specific ticket activity")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", 
                   description = "Comment created successfully", 
                   content = @Content(schema = @Schema(implementation = TicketActivityCommentResponse.class))),
        @ApiResponse(responseCode = "400", 
                   description = "Invalid comment data"),
        @ApiResponse(responseCode = "404", 
                   description = "Ticket or activity not found")
    })
    @PostMapping
    public ResponseEntity<TicketActivityCommentResponse> createComment(
            @Parameter(description = "Unique identifier of the ticket", required = true)
            @PathVariable UUID ticketId,
            @Parameter(description = "Unique identifier of the activity", required = true)
            @PathVariable Long activityId,
            @Parameter(description = "Comment details", required = true)
            @Valid @RequestBody TicketActivityCommentRequest request) {
        
        // Set authenticated user as the comment author
        String username = authUtils.getAuthenticatedUsername();
        request.setUsername(username);
        
        TicketActivityCommentResponse response = commentService.createComment(ticketId, activityId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves all comments for a ticket activity.
     *
     * @param ticketId The ID of the ticket
     * @param activityId The ID of the activity
     * @return A response entity with the list of comments
     */
    @Operation(summary = "Get all comments", 
              description = "Retrieves all comments for a specific ticket activity")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                   description = "Comments retrieved successfully", 
                   content = @Content(schema = @Schema(implementation = TicketActivityCommentResponse.class))),
        @ApiResponse(responseCode = "404", 
                   description = "Ticket or activity not found")
    })
    @GetMapping
    public ResponseEntity<List<TicketActivityCommentResponse>> getAllComments(
            @Parameter(description = "Unique identifier of the ticket", required = true)
            @PathVariable UUID ticketId,
            @Parameter(description = "Unique identifier of the activity", required = true)
            @PathVariable Long activityId) {
        
        List<TicketActivityCommentResponse> comments = commentService.getCommentsByActivityId(ticketId, activityId);
        return ResponseEntity.ok(comments);
    }

    /**
     * Retrieves a specific comment by its ID.
     *
     * @param ticketId The ID of the ticket
     * @param activityId The ID of the activity
     * @param commentId The ID of the comment
     * @return A response entity with the comment
     */
    @Operation(summary = "Get comment by ID", 
              description = "Retrieves a specific comment by its identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                   description = "Comment retrieved successfully", 
                   content = @Content(schema = @Schema(implementation = TicketActivityCommentResponse.class))),
        @ApiResponse(responseCode = "404", 
                   description = "Comment, ticket, or activity not found")
    })
    @GetMapping("/{commentId}")
    public ResponseEntity<TicketActivityCommentResponse> getCommentById(
            @Parameter(description = "Unique identifier of the ticket", required = true)
            @PathVariable UUID ticketId,
            @Parameter(description = "Unique identifier of the activity", required = true)
            @PathVariable Long activityId,
            @Parameter(description = "Unique identifier of the comment", required = true)
            @PathVariable Long commentId) {
        
        TicketActivityCommentResponse comment = commentService.getCommentById(ticketId, activityId, commentId);
        return ResponseEntity.ok(comment);
    }

    /**
     * Updates an existing comment.
     *
     * @param ticketId The ID of the ticket
     * @param activityId The ID of the activity
     * @param commentId The ID of the comment to update
     * @param request The updated comment data
     * @return A response entity with the updated comment
     */
    @Operation(summary = "Update a comment", 
              description = "Updates the content of an existing comment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", 
                   description = "Comment updated successfully", 
                   content = @Content(schema = @Schema(implementation = TicketActivityCommentResponse.class))),
        @ApiResponse(responseCode = "400", 
                   description = "Invalid comment data"),
        @ApiResponse(responseCode = "404", 
                   description = "Comment, ticket, or activity not found")
    })
    @PutMapping("/{commentId}")
    public ResponseEntity<TicketActivityCommentResponse> updateComment(
            @Parameter(description = "Unique identifier of the ticket", required = true)
            @PathVariable UUID ticketId,
            @Parameter(description = "Unique identifier of the activity", required = true)
            @PathVariable Long activityId,
            @Parameter(description = "Unique identifier of the comment", required = true)
            @PathVariable Long commentId,
            @Parameter(description = "Updated comment details", required = true)
            @Valid @RequestBody TicketActivityCommentRequest request) {
        
        // Set authenticated user as the comment author
        String username = authUtils.getAuthenticatedUsername();
        request.setUsername(username);
        
        TicketActivityCommentResponse response = commentService.updateComment(ticketId, activityId, commentId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a comment (soft delete).
     *
     * @param ticketId The ID of the ticket
     * @param activityId The ID of the activity
     * @param commentId The ID of the comment to delete
     * @return A response entity with no content
     */
    @Operation(summary = "Delete a comment", 
              description = "Soft-deletes a comment from a ticket activity")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", 
                   description = "Comment deleted successfully"),
        @ApiResponse(responseCode = "404", 
                   description = "Comment, ticket, or activity not found")
    })
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "Unique identifier of the ticket", required = true)
            @PathVariable UUID ticketId,
            @Parameter(description = "Unique identifier of the activity", required = true)
            @PathVariable Long activityId,
            @Parameter(description = "Unique identifier of the comment", required = true)
            @PathVariable Long commentId) {
        
        commentService.deleteComment(ticketId, activityId, commentId);
        return ResponseEntity.noContent().build();
    }
}