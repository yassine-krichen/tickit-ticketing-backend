package com.sofrecom.tickit.service;

import com.sofrecom.tickit.model.Ticket;
import com.sofrecom.tickit.model.CaseType;
import com.sofrecom.tickit.dto.TicketCount;
import com.sofrecom.tickit.dto.TicketUpdateDTO;
import com.sofrecom.tickit.exception.EntityCreationException;
import org.springframework.data.domain.Page;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service interface for managing {@link Ticket} entities.
 */
public interface TicketService {

    Ticket createTicket(Ticket ticket) throws EntityCreationException;

    Ticket getTicketById(UUID ticketId);

    List<Ticket> getAllTickets(Ticket.Status status, Ticket.Priority priority, String issuedBy, String acteTraitement);
    
    // Enhanced search with the new fields
    List<Ticket> searchTickets(Ticket.Status status, Ticket.Priority priority, String issuedBy, 
                              String acteTraitement, CaseType type, CaseType step, String subStatus);

    Ticket updateTicketStatus(UUID ticketId, Ticket.Status status, String userFingerprint) throws EntityCreationException;

    Ticket updateTicket(UUID ticketId, TicketUpdateDTO ticketRequest) throws EntityCreationException;

    void deleteTicket(UUID ticketId) throws EntityCreationException;

    List<TicketCount> getTicketCountByStatus();
    
    // New methods for SLA calculation
    Duration calculateResponseSLA(CaseType type, CaseType step, Ticket.Priority priority);
    
    Duration calculateResolutionSLA(CaseType type, CaseType step, Ticket.Priority priority);
    
    // New reporting methods
    List<TicketCount> getTicketCountByType();
    
    List<TicketCount> getTicketCountByStep();
    
    List<TicketCount> getTicketCountBySubStatus();
    
    Map<String, Long> getTicketStatsByPeriod(String period);
    
    // New method to validate type and step relationship
    boolean isValidTypeStepCombination(CaseType type, CaseType step);
    
    // Get valid steps for a given type
    List<CaseType> getValidStepsForType(CaseType type);

    /**
     * Retrieves a paginated and filtered list of tickets.
     *
     * @param page       The page number (0-based).
     * @param size       The number of items per page.
     * @param sortField  The field to sort by (e.g., "createdAt", "subject", "priority", "status").
     * @param sortDir    The sort direction ("asc" or "desc").
     * @param q          Optional free-text search query for subject/description.
     * @param statuses   Optional filter by ticket statuses (supports multiple values).
     * @param priorities Optional filter by ticket priorities (supports multiple values).
     * @param types      Optional filter by ticket types (supports multiple values).
     * @param steps      Optional filter by ticket steps (supports multiple values).
     * @return A Page containing the requested Ticket entities.
     * @throws com.sofrecom.tickit.exception.InvalidSortParameterException if sortField or sortDir is invalid.
     */
    Page<Ticket> getTickets(
            int page,
            int size,
            String sortField,
            String sortDir,
            String q,
            String category,
            List<Ticket.Status> statuses,
            List<Ticket.Priority> priorities,
            List<CaseType> types,
            List<CaseType> steps
    );

    /**
     * Calculates month-over-month comparisons for ticket statistics.
     * 
     * @return Map with percentage changes for different metrics:
     *         - totalTickets: Change in total tickets
     *         - inProgress: Change in IN_PROGRESS tickets
     *         - blocked: Change in BLOCKED tickets
     *         - resolved: Change in RESOLVED tickets
     */
    Map<String, Double> getMonthOverMonthComparisons();

    public List<Ticket> findSimilarResolvedTickets(String subject, CaseType type, int limit);

    public Map<String, Object> getTicketTrends(int days);

    public Map<String, Double> getResolutionMetrics();

    /**
     * Automatically deletes tickets that are in 'CLOSED' status and whose 'updatedAt'
     * timestamp is older than 90 days. This method is designed to be invoked by
     * a scheduled task.
     *
     * @return The number of tickets that were deleted.
     */
    int autoDeleteOldClosedTickets();
}
