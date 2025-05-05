package com.sofrecom.tickit.impl;

import com.sofrecom.tickit.dto.TicketActivityCommentRequest;
import com.sofrecom.tickit.dto.TicketActivityCommentResponse;
import com.sofrecom.tickit.exception.ResourceNotFoundException;
import com.sofrecom.tickit.mapper.TicketActivityCommentMapper;
import com.sofrecom.tickit.model.Ticket;
import com.sofrecom.tickit.model.TicketActivity;
import com.sofrecom.tickit.model.TicketActivityComment;
import com.sofrecom.tickit.repository.TicketActivityCommentRepository;
import com.sofrecom.tickit.repository.TicketActivityRepository;
import com.sofrecom.tickit.repository.TicketRepository;
import com.sofrecom.tickit.service.TicketActivityCommentService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of the TicketActivityCommentService interface.
 * Provides business logic for managing ticket activity comments.
 */
@Service
@RequiredArgsConstructor
public class TicketActivityCommentServiceImpl implements TicketActivityCommentService {

    private final TicketRepository ticketRepository;
    private final TicketActivityRepository activityRepository;
    private final TicketActivityCommentRepository commentRepository;
    private final TicketActivityCommentMapper commentMapper;

    @Override
    @Transactional
    public TicketActivityCommentResponse createComment(UUID ticketId, Long activityId, TicketActivityCommentRequest request) {
        // Validate ticket exists
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", "ID", ticketId));

        // Validate activity exists and belongs to the specified ticket
        TicketActivity activity = activityRepository.findByIdAndTicketId(activityId, ticket.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Activity", "ID", activityId));

        // Create and save the comment
        TicketActivityComment comment = TicketActivityComment.builder()
                .activity(activity)
                .content(request.getContent())
                .username(request.getUsername())
                .build();

        comment = commentRepository.save(comment);
        return commentMapper.toResponse(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketActivityCommentResponse> getCommentsByActivityId(UUID ticketId, Long activityId) {
        // Validate ticket exists
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", "ID", ticketId));

        // Validate activity exists and belongs to the specified ticket
        TicketActivity activity = activityRepository.findByIdAndTicketId(activityId, ticket.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Activity", "ID", activityId));

        // Retrieve and map all comments
        List<TicketActivityComment> comments = commentRepository.findByActivityIdOrderByCreatedAtDesc(activity.getId());
        return comments.stream()
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TicketActivityCommentResponse getCommentById(UUID ticketId, Long activityId, Long commentId) {
        TicketActivityComment comment = getValidatedComment(ticketId, activityId, commentId);
        return commentMapper.toResponse(comment);
    }

    @Override
    @Transactional
    public TicketActivityCommentResponse updateComment(UUID ticketId, Long activityId, Long commentId, 
                                                     TicketActivityCommentRequest request) {
        TicketActivityComment comment = getValidatedComment(ticketId, activityId, commentId);
        
        // Update only the content to avoid changing other fields
        comment.setContent(request.getContent());
        
        // Use saveAndFlush to ensure the @PreUpdate is triggered
        comment = commentRepository.saveAndFlush(comment);
        return commentMapper.toResponse(comment);
    }

    @Override
    @Transactional
    public void deleteComment(UUID ticketId, Long activityId, Long commentId) {
        TicketActivityComment comment = getValidatedComment(ticketId, activityId, commentId);
        
        // Soft delete
        comment.setDeleted(true);
        comment.setDeletedAt(LocalDateTime.now());
        
        commentRepository.save(comment);
    }
    
    /**
     * Helper method to validate and retrieve a comment.
     * Ensures the comment exists and belongs to the specified activity and ticket.
     *
     * @param ticketId The ticket ID
     * @param activityId The activity ID
     * @param commentId The comment ID
     * @return The validated comment
     * @throws ResourceNotFoundException if any resource is not found
     */
    private TicketActivityComment getValidatedComment(UUID ticketId, Long activityId, Long commentId) {
        // Validate ticket exists
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", "ID", ticketId));

        // Validate activity exists and belongs to the specified ticket
        TicketActivity activity = activityRepository.findByIdAndTicketId(activityId, ticket.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Activity", "ID", activityId));

        // Validate comment exists
        TicketActivityComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "ID", commentId));

        // Validate comment belongs to the specified activity
        if (!comment.getActivity().getId().equals(activity.getId())) {
            throw new ResourceNotFoundException("Comment", "ID", commentId); 
        }
        
        return comment;
    }
}