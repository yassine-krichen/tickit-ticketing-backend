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

/**
 * Repository interface for ticket activities.
 * Provides methods for database operations on TicketActivity entities.
 */
public interface TicketActivityRepository extends 
        PagingAndSortingRepository<TicketActivity, Long>,
        JpaSpecificationExecutor<TicketActivity> {
    
    /**
     * Finds an activity by its ID and the ticket it belongs to.
     *
     * @param id The activity ID
     * @param ticketId The ticket ID
     * @return Optional containing the activity if found
     */
    Optional<TicketActivity> findByIdAndTicket_Id(Long id, UUID ticketId);
    
    /**
     * Alternative method name for finding an activity by ID and ticket ID.
     * This matches the method name used in the service implementation.
     *
     * @param id The activity ID
     * @param ticketId The ticket ID
     * @return Optional containing the activity if found
     */
    default Optional<TicketActivity> findByIdAndTicketId(Long id, UUID ticketId) {
        return findByIdAndTicket_Id(id, ticketId);
    }

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
     *
     * @param threshold The cutoff timestamp for deletion
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM TicketActivity a WHERE a.deleted = true AND a.deletedAt < :threshold")
    void deleteAllSoftDeletedBefore(LocalDateTime threshold);
}