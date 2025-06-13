package com.sofrecom.tickit.repository;

import com.sofrecom.tickit.model.Ticket;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link Ticket} entities.
 * Provides CRUD operations, pagination, sorting, and specification-based querying.
 */
@Repository
public interface TicketRepository extends PagingAndSortingRepository<Ticket, UUID>, JpaSpecificationExecutor<Ticket> {
    // JpaSpecificationExecutor provides findAll(Specification<T> spec, Pageable pageable)
    // PagingAndSortingRepository provides findAll(Pageable pageable)

    /**
     * Deletes tickets that have the specified status and were last updated before the given timestamp.
     * This is a bulk operation and will not trigger JPA lifecycle callbacks on individual entities.
     *
     * @param status The status of the tickets to delete (e.g., CLOSED).
     * @param ninetyDaysAgo The timestamp threshold; tickets updated before this time will be considered for deletion.
     * @return The number of tickets deleted.
     */
    @Modifying
    @Query("DELETE FROM Ticket t WHERE t.status = :status AND t.updatedAt < :ninetyDaysAgo")
    int deleteTicketsByStatusAndUpdatedAtBefore(@Param("status") Ticket.Status status, @Param("ninetyDaysAgo") LocalDateTime ninetyDaysAgo);
}
