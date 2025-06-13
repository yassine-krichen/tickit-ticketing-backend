package com.sofrecom.tickit.repository;

import com.sofrecom.tickit.model.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link Ticket} entities.
 * Provides CRUD operations, pagination, sorting, and specification-based querying.
 */
@Repository
public interface TicketRepository extends PagingAndSortingRepository<Ticket, UUID>, JpaSpecificationExecutor<Ticket> {
    
    // Find similar resolved tickets based on keywords
    @Query("SELECT t FROM Ticket t WHERE " +
           "LOWER(t.subject) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "AND t.status = :status " +
           "AND (:type IS NULL OR t.type = :type) " +
           "ORDER BY t.resolvedAt DESC")
    Page<Ticket> findSimilarResolvedTickets(@Param("keyword") String keyword,
                                           @Param("status") Ticket.Status status,
                                           @Param("type") String type,
                                           Pageable pageable);
   
    // Alternative method using List if you prefer
    @Query("SELECT t FROM Ticket t WHERE " +
           "LOWER(t.subject) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "AND t.status = :status " +
           "AND (:type IS NULL OR t.type = :type) " +
           "ORDER BY t.resolvedAt DESC")
    List<Ticket> findSimilarResolvedTicketsList(@Param("keyword") String keyword,
                                               @Param("status") Ticket.Status status,
                                               @Param("type") String type,
                                               Pageable pageable);
   
    // FIXED: Get ticket trends - corrected table name and database compatibility
    @Query(value = "SELECT DATE(created_at) as date, COUNT(*) as count " +
                   "FROM ticket t " +
                   "WHERE t.created_at >= :startDate " +
                   "GROUP BY DATE(created_at) " +
                   "ORDER BY date DESC", 
           nativeQuery = true)
    List<Object[]> getTicketTrends(@Param("startDate") LocalDateTime startDate);
   
    // FIXED: Get resolution metrics - corrected table name and parameter type
    @Query(value = "SELECT " +
               "AVG(EXTRACT(EPOCH FROM (resolved_at - created_at))/3600) as avgHours, " +
               "MIN(EXTRACT(EPOCH FROM (resolved_at - created_at))/3600) as minHours, " +
               "MAX(EXTRACT(EPOCH FROM (resolved_at - created_at))/3600) as maxHours " +
               "FROM ticket t " +
               "WHERE t.status = :status AND t.resolved_at IS NOT NULL", 
       nativeQuery = true)
Object[] getResolutionMetrics(@Param("status") String status);
   
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