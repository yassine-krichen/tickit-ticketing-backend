package com.sofrecom.tickit.repository;

import com.sofrecom.tickit.model.Ticket;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for creating JPA Specifications for filtering Tickets.
 */
public class TicketSpecification {

    /**
     * Creates a Specification based on optional filter criteria.
     *
     * @param q        Optional free-text search query for subject or description.
     * @param status   Optional status filter.
     * @param priority Optional priority filter.
     * @return A Specification<Ticket> combining the provided filters.
     */
    public static Specification<Ticket> filterBy(
            String q,
            Ticket.Status status,
            Ticket.Priority priority) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Add status filter if provided
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            // Add priority filter if provided
            if (priority != null) {
                predicates.add(criteriaBuilder.equal(root.get("priority"), priority));
            }

            // Add full-text search filter if 'q' is provided
            if (StringUtils.hasText(q)) {
                String likePattern = "%" + q.toLowerCase() + "%";
                Predicate subjectLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("subject")), likePattern);
                Predicate descriptionLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), likePattern);
                predicates.add(criteriaBuilder.or(subjectLike, descriptionLike));
            }

            // Combine all predicates with AND
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}