package com.sofrecom.tickit.repository;

import com.sofrecom.tickit.model.TicketActivity;
import com.sofrecom.tickit.model.TicketActivity.ActivityType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;

public interface TicketActivityRepository extends 
        PagingAndSortingRepository<TicketActivity, Long>,
        JpaSpecificationExecutor<TicketActivity> {
    
    Optional<TicketActivity> findByIdAndTicket_Id(Long id, UUID ticketId);

    Page<TicketActivity> findByTicket_Id(UUID ticketId, Pageable pageable);

    Page<TicketActivity> findByTicket_IdAndType(UUID ticketId,
                                                ActivityType type,
                                                Pageable pageable);

    Page<TicketActivity> findByTicket_IdAndUsername(UUID ticketId,
                                                    String username,
                                                    Pageable pageable);

    Page<TicketActivity> findByTicket_IdAndTypeAndUsername(UUID ticketId,
                                                           ActivityType type,
                                                           String username,
                                                           Pageable pageable);

    /**
     * Permanently deletes all activities soft-deleted before the given threshold.
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM TicketActivity a WHERE a.deleted = true AND a.deletedAt < :threshold")
    void deleteAllSoftDeletedBefore(LocalDateTime threshold);
}