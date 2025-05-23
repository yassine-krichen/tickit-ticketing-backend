package com.sofrecom.tickit.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.sofrecom.tickit.dto.TicketActivityRequest;
import com.sofrecom.tickit.dto.TicketActivityResponse;
import com.sofrecom.tickit.exception.InvalidSortParameterException;
import com.sofrecom.tickit.exception.ResourceNotFoundException;
import com.sofrecom.tickit.model.Ticket;
import com.sofrecom.tickit.model.TicketActivity;
import com.sofrecom.tickit.model.TicketActivity.ActivityType;
import com.sofrecom.tickit.repository.TicketActivityRepository;
import com.sofrecom.tickit.repository.TicketActivitySpecification;
import com.sofrecom.tickit.repository.TicketRepository;
import com.sofrecom.tickit.service.TicketActivityService;
import org.springframework.context.ApplicationEventPublisher;

import javax.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.sofrecom.tickit.listener.ActivityCreatedEvent;
import lombok.RequiredArgsConstructor;

/**
 * Implementation of the TicketActivityService interface.
 * Manages the lifecycle of ticket activities.
 */
@Service
@RequiredArgsConstructor
public class TicketActivityServiceImpl implements TicketActivityService {
    private final TicketActivityRepository activityRepo;
    private final TicketRepository ticketRepo;
    private final ApplicationEventPublisher eventPublisher;
    
    // Define allowed sort fields
    private static final List<String> ALLOWED_SORT_FIELDS = Arrays.asList(
            "startTime", 
            "createdAt",
            "type",
            "username"
    );

    @Override
    @Transactional
    public TicketActivityResponse createActivity(UUID ticketId,
                                                 TicketActivityRequest request) {
        Ticket ticket = ticketRepo.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));
        
        TicketActivity entity = new TicketActivity();
        entity.setTicket(ticket);
        entity.setUsername(request.getUsername());
        entity.setType(request.getType());
        entity.setDescription(request.getDescription().trim());
        
        // Auto-complete OPEN and CLOSE activities
        if (request.getType() == ActivityType.OPEN || request.getType() == ActivityType.CLOSE) {
            LocalDateTime now = LocalDateTime.now();
            // Set end time to match start time for instant completion
            entity.setEndTime(now);
            // Duration will be calculated to 0 by the @PostLoad method
        }
        
        activityRepo.save(entity);
        
        // Instead of directly updating ticket status, publish an event
        if (request.getType() != ActivityType.OPEN && 
            request.getType() != ActivityType.CLOSE && 
            request.getType() != ActivityType.UPDATE) {
            eventPublisher.publishEvent(new ActivityCreatedEvent(entity));
        }
        
        return toResponse(entity);
    }

    @Override
    @Transactional
    public TicketActivityResponse updateActivity(UUID ticketId,
                                                 Long activityId,
                                                 TicketActivityRequest request) {
        TicketActivity entity = activityRepo.findByIdAndTicket_Id(activityId, ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Activity not found"));
        entity.setType(request.getType());
        entity.setDescription(request.getDescription());
        activityRepo.save(entity);
        return toResponse(entity);
    }

    @Override
    @Transactional
    public TicketActivityResponse completeActivity(UUID ticketId,
                                                   Long activityId) {
        TicketActivity entity = activityRepo.findByIdAndTicket_Id(activityId, ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Activity not found"));
        entity.setEndTime(LocalDateTime.now());
        entity.setDuration(Duration.between(entity.getStartTime(), LocalDateTime.now()));
        activityRepo.save(entity);
        return toResponse(entity);
    }

    @Override
    @Transactional
    public void deleteActivity(UUID ticketId,
                               Long activityId) {
        TicketActivity entity = activityRepo.findByIdAndTicket_Id(activityId, ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Activity not found"));
        entity.setDeleted(true);
        entity.setDeletedAt(LocalDateTime.now());
        activityRepo.save(entity);
    }

    @Override
    public Page<TicketActivityResponse> getTimeline(
            UUID ticketId,
            String q,
            ActivityType type,
            String username,
            int page,
            int size,
            String sortField,
            String sortDir) {
        
        // Validate and get actual sort field
        String actualSortField = validateSortField(sortField);
        
        // Validate and get sort direction
        Sort.Direction direction = validateSortDirection(sortDir);
        
        // Create pageable with sort
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, actualSortField));
        
        // Create specification with filters
        Specification<TicketActivity> spec = 
            TicketActivitySpecification.filterBy(ticketId, q, type, username);
        
        // Query using specification
        Page<TicketActivity> activityPage = activityRepo.findAll(spec, pageable);
        
        // Map to DTOs
        return activityPage.map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public TicketActivityResponse getActivityById(UUID ticketId, Long activityId) {
        TicketActivity activity = activityRepo.findByIdAndTicket_Id(activityId, ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Activity", "ID", activityId));
        return toResponse(activity);
    }

    /**
     * Validates the sort field and returns the actual entity field name.
     *
     * @param sortField The sort field provided by the user.
     * @return The validated sort field.
     * @throws InvalidSortParameterException If the sort field is invalid.
     */
    private String validateSortField(String sortField) {
        if (!ALLOWED_SORT_FIELDS.contains(sortField)) {
            throw new InvalidSortParameterException(
                    "Invalid sort field: '" + sortField + "'. Allowed fields are: " +
                    String.join(", ", ALLOWED_SORT_FIELDS)
            );
        }
        return sortField;
    }
    
    /**
     * Validates the sort direction string.
     *
     * @param sortDir The sort direction string ("asc" or "desc", case-insensitive).
     * @return The corresponding Sort.Direction enum.
     * @throws InvalidSortParameterException If the direction is invalid.
     */
    private Sort.Direction validateSortDirection(String sortDir) {
        if ("asc".equalsIgnoreCase(sortDir)) {
            return Sort.Direction.ASC;
        } else if ("desc".equalsIgnoreCase(sortDir)) {
            return Sort.Direction.DESC;
        } else {
            throw new InvalidSortParameterException(
                    "Invalid sort direction: '" + sortDir + "'. Allowed directions are: 'asc', 'desc'."
            );
        }
    }
    
    private TicketActivityResponse toResponse(TicketActivity entity) {
        TicketActivityResponse dto = new TicketActivityResponse();
        dto.setId(entity.getId());
        dto.setTicketId(entity.getTicket().getId());
        dto.setUsername(entity.getUsername());
        dto.setType(entity.getType());
        dto.setDescription(entity.getDescription());
        dto.setStartTime(entity.getStartTime());
        dto.setEndTime(entity.getEndTime());
        dto.setDuration(entity.getDuration());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}