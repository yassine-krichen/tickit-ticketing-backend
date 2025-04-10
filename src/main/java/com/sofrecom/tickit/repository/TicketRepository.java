package com.sofrecom.tickit.repository;

import com.sofrecom.tickit.dto.TicketCount;
import com.sofrecom.tickit.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> , JpaSpecificationExecutor<Ticket>{

    @Query("SELECT new com.sofrecom.tickit.dto.TicketCountDTO(t.status, COUNT(t)) " +
           "FROM Ticket t GROUP BY t.status")
    List<TicketCount> countTicketsByStatus();
}
