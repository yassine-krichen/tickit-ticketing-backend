package com.sofrecom.tickit.repository;

import com.sofrecom.tickit.model.CaseType;
import com.sofrecom.tickit.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID>, JpaSpecificationExecutor<Ticket> {

    // SQL View equivalent: Find tickets by type
    @Query("SELECT t FROM Ticket t WHERE t.type = :type")
    List<Ticket> findByType(@Param("type") CaseType type);
    
    // SQL View equivalent: Find tickets by step
    @Query("SELECT t FROM Ticket t WHERE t.step = :step")
    List<Ticket> findByStep(@Param("step") CaseType step);
    
    // SQL View equivalent: Find tickets by type and step
    @Query("SELECT t FROM Ticket t WHERE t.type = :type AND t.step = :step")
    List<Ticket> findByTypeAndStep(@Param("type") CaseType type, @Param("step") CaseType step);
    
    // SQL View equivalent: Find tickets by sub-status
    @Query("SELECT t FROM Ticket t WHERE t.subStatus = :subStatus")
    List<Ticket> findBySubStatus(@Param("subStatus") String subStatus);
    
    // SQL View equivalent: Find tickets created within a date range
    @Query("SELECT t FROM Ticket t WHERE t.createdAt BETWEEN :startDate AND :endDate")
    List<Ticket> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // SQL View equivalent: Count tickets by type
    @Query("SELECT t.type as type, COUNT(t) as count FROM Ticket t GROUP BY t.type")
    List<Object[]> countByType();
    
    // SQL View equivalent: Count tickets by step
    @Query("SELECT t.step as step, COUNT(t) as count FROM Ticket t GROUP BY t.step")
    List<Object[]> countByStep();
    
    // SQL View equivalent: Count tickets by sub-status
    @Query("SELECT t.subStatus as subStatus, COUNT(t) as count FROM Ticket t GROUP BY t.subStatus")
    List<Object[]> countBySubStatus();
    
    // SQL View equivalent: SLA compliance report
    @Query("SELECT t FROM Ticket t WHERE " +
           "((t.status = 'RESOLVED' OR t.status = 'CLOSED') AND " +
           " t.updatedAt > (t.createdAt + t.resolutionSla))")
    List<Ticket> findBySLABreached();
    
    // SQL View equivalent: Custom reporting view for management dashboard
    @Query("SELECT t.type as type, t.step as step, t.status as status, COUNT(t) as count " +
           "FROM Ticket t " +
           "WHERE t.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY t.type, t.step, t.status")
    List<Object[]> generateManagementReport(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
