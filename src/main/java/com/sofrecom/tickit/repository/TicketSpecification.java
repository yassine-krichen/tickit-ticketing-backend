package com.sofrecom.tickit.repository;

import com.sofrecom.tickit.model.CaseType;
import com.sofrecom.tickit.model.Ticket;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
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

    /**
     * Creates a Specification for finding similar resolved tickets.
     *
     * @param keyword The keyword to search for in ticket subjects.
     * @param status  The ticket status to filter by.
     * @param type    Optional ticket type filter.
     * @return A Specification<Ticket> for similar resolved tickets.
     */
    public static Specification<Ticket> findSimilarTickets(
            String keyword,
            Ticket.Status status,
            String type) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Add keyword search in subject
            if (StringUtils.hasText(keyword)) {
                String likePattern = "%" + keyword.toLowerCase() + "%";
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("subject")), likePattern));
            }
            
            // Add status filter
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            
            // Add type filter if provided
            if (StringUtils.hasText(type)) {
                predicates.add(criteriaBuilder.equal(root.get("type"), type));
            }
            
            // Add ordering by resolvedAt DESC
            if (query != null) {
                query.orderBy(criteriaBuilder.desc(root.get("resolvedAt")));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
    
    /**
     * Creates a Specification for tickets created after a specific date.
     *
     * @param startDate The start date for filtering tickets.
     * @return A Specification<Ticket> for tickets created after the start date.
     */
    public static Specification<Ticket> createdAfter(LocalDateTime startDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDate);
            }
            return criteriaBuilder.conjunction();
        };
    }
    
    /**
     * Creates a Specification for resolved tickets only.
     *
     * @return A Specification<Ticket> for resolved tickets with non-null resolvedAt.
     */
    public static Specification<Ticket> isResolved() {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.and(
                criteriaBuilder.equal(root.get("status"), Ticket.Status.RESOLVED),
                criteriaBuilder.isNotNull(root.get("resolvedAt"))
            );
        };
    }

    public static Specification<Ticket> hasAnyType(List<CaseType> types) {
        return (root, query, cb) -> {
            if (types == null || types.isEmpty()) {
                return null;
            }
            return root.get("type").in(types);
        };
    }

    public static Specification<Ticket> hasAnyStep(List<CaseType> steps) {
        return (root, query, cb) -> {
            if (steps == null || steps.isEmpty()) {
                return null;
            }
            return root.get("step").in(steps);
        };
    }

    public static Specification<Ticket> containsText(String searchText) {
        return (root, query, cb) -> {
            if (searchText == null || searchText.trim().isEmpty()) {
                return null;
            }
            String pattern = "%" + searchText.toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("subject")), pattern),
                cb.like(cb.lower(root.get("description")), pattern)
            );
        };
    }

    public static Specification<Ticket> hasStatus(Ticket.Status status) {
        return (root, query, cb) -> status == null ? null : 
            cb.equal(root.get("status"), status);
    }

    public static Specification<Ticket> hasPriority(Ticket.Priority priority) {
        return (root, query, cb) -> priority == null ? null : 
            cb.equal(root.get("priority"), priority);
    }

    /**
     * Creates a Specification for tickets with any of the specified statuses.
     *
     * @param statuses List of statuses to filter by
     * @return A Specification<Ticket> for tickets with any of the specified statuses
     */
    public static Specification<Ticket> hasAnyStatus(List<Ticket.Status> statuses) {
        return (root, query, cb) -> {
            if (statuses == null || statuses.isEmpty()) {
                return null;
            }
            return root.get("status").in(statuses);
        };
    }

    /**
     * Creates a Specification for tickets with any of the specified priorities.
     *
     * @param priorities List of priorities to filter by
     * @return A Specification<Ticket> for tickets with any of the specified priorities
     */
    public static Specification<Ticket> hasAnyPriority(List<Ticket.Priority> priorities) {
        return (root, query, cb) -> {
            if (priorities == null || priorities.isEmpty()) {
                return null;
            }
            return root.get("priority").in(priorities);
        };
    }

    public static Specification<Ticket> containsTextByCategory(String searchText, String category) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(searchText)) {
                return cb.conjunction();
            }
            String pattern = "%" + searchText.toLowerCase() + "%";
            if ("description".equalsIgnoreCase(category)) {
                return cb.like(cb.lower(root.get("description")), pattern);
            }
            // Default to searching in subject for any other category value (e.g., "title")
            return cb.like(cb.lower(root.get("subject")), pattern);
        };
    }
}