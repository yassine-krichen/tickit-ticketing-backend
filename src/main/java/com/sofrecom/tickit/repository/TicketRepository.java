package com.sofrecom.tickit.repository;

import com.sofrecom.tickit.model.Ticket;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Spring Data JPA repository for {@link Ticket} entities.
 * Provides CRUD operations, pagination, sorting, and specification-based querying.
 */
@Repository
public interface TicketRepository extends PagingAndSortingRepository<Ticket, UUID>, JpaSpecificationExecutor<Ticket> {
    // JpaSpecificationExecutor provides findAll(Specification<T> spec, Pageable pageable)
    // PagingAndSortingRepository provides findAll(Pageable pageable)
}
