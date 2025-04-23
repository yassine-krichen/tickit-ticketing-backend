package com.sofrecom.tickit.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sofrecom.tickit.dto.TicketActivityRequest;
import com.sofrecom.tickit.dto.TicketActivityResponse;
import com.sofrecom.tickit.model.Ticket;
import com.sofrecom.tickit.model.TicketActivity;
import com.sofrecom.tickit.model.TicketActivity.ActivityType;
import com.sofrecom.tickit.repository.TicketActivityRepository;
import com.sofrecom.tickit.repository.TicketRepository;
import com.sofrecom.tickit.service.TicketActivityService;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.UUID;

@Service
public class TicketActivityServiceImpl implements TicketActivityService {
private final TicketActivityRepository activityRepo;
    private final TicketRepository ticketRepo;

    public TicketActivityServiceImpl(TicketActivityRepository activityRepo,
                                     TicketRepository ticketRepo) {
        this.activityRepo = activityRepo;
        this.ticketRepo = ticketRepo;
    }

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
        activityRepo.save(entity);
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
    public Page<TicketActivityResponse> getTimeline(UUID ticketId,
                                                    ActivityType type,
                                                    String username,
                                                    Pageable pageable) {
        Page<TicketActivity> page;
        if (type != null && username != null) {
            page = activityRepo.findByTicket_IdAndTypeAndUsername(ticketId, type, username, pageable);
        } else if (type != null) {
            page = activityRepo.findByTicket_IdAndType(ticketId, type, pageable);
        } else if (username != null) {
            page = activityRepo.findByTicket_IdAndUsername(ticketId, username, pageable);
        } else {
            page = activityRepo.findByTicket_Id(ticketId, pageable);
        }
        return page.map(this::toResponse);
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