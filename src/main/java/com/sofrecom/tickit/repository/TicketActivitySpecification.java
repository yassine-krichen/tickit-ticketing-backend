package com.sofrecom.tickit.repository;

import com.sofrecom.tickit.model.TicketActivity;
import com.sofrecom.tickit.model.TicketActivity.ActivityType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Utility class for creating JPA Specifications for filtering TicketActivities.
 */
public class TicketActivitySpecification {

    /**
     * Creates a Specification based on optional filter criteria.
     *
     * @param ticketId  UUID of the ticket to filter activities for
     * @param q         Optional search query for description
     * @param type      Optional filter by activity type
     * @param username  Optional filter by username
     * @return A Specification<TicketActivity> combining the provided filters
     */
    public static Specification<TicketActivity> filterBy(
            UUID ticketId,
            String q,
            ActivityType type,
            String username) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Mandatory filter by ticket ID
            predicates.add(criteriaBuilder.equal(root.get("ticket").get("id"), ticketId));
            
            // Filter by activity type if provided
            if (type != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), type));
            }

            // Filter by username if provided
            if (StringUtils.hasText(username)) {
                predicates.add(criteriaBuilder.equal(root.get("username"), username));
            }

            // Add search query filter if provided
            if (StringUtils.hasText(q)) {
                String likePattern = "%" + q.toLowerCase() + "%";
                Predicate descriptionLike = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("description")), 
                    likePattern
                );
                predicates.add(descriptionLike);
            }

            // Add default filter for non-deleted activities
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));

            // Combine all predicates with AND
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}