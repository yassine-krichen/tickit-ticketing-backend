package com.sofrecom.tickit.impl;

import com.sofrecom.tickit.dto.TicketCount;
import com.sofrecom.tickit.dto.TicketUpdateDTO;
import com.sofrecom.tickit.exception.EntityCreationException;
import com.sofrecom.tickit.exception.InvalidSortParameterException;
import com.sofrecom.tickit.model.CaseType;
import com.sofrecom.tickit.model.Ticket;
import com.sofrecom.tickit.repository.TicketRepository;
import com.sofrecom.tickit.repository.TicketSpecification;
import com.sofrecom.tickit.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the TicketService interface.
 */
@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;

    @PersistenceContext
    private EntityManager entityManager;

    // Map defining valid type-step combinations
    private static final Map<CaseType, List<CaseType>> VALID_TYPE_STEP_MAPPINGS = initTypeStepMappings();

    // SLA configurations based on type, step, and priority
    private static final Map<String, Duration> RESPONSE_SLA_CONFIGS = initResponseSlaConfigs();
    private static final Map<String, Duration> RESOLUTION_SLA_CONFIGS = initResolutionSlaConfigs();

    // Define allowed sort fields
    private static final List<String> ALLOWED_SORT_FIELDS = Arrays.asList(
            "createdAt", // Default mapping for "timestamp"
            "subject",   // Default mapping for "title"
            "priority",
            "status"
    );

    private static Map<CaseType, List<CaseType>> initTypeStepMappings() {
        Map<CaseType, List<CaseType>> mappings = new HashMap<>();

        // GRAFIC case type can only have GRAFIC as step
        mappings.put(CaseType.GRAFIC, Collections.singletonList(CaseType.GRAFIC));

        // BRAME case type with its valid steps
        mappings.put(CaseType.BRAME, Arrays.asList(
                CaseType.BRAME_PRE_ETUDE, CaseType.BRAME_ETUDE,
                CaseType.BRAME_RACCORDEMENT, CaseType.BRAME_CONTROLE_TRAVAUX
        ));

        // XOIP case type with its valid steps
        mappings.put(CaseType.XOIP, Arrays.asList(
                CaseType.AFFECTATION_ET_MISE_EN_CAUSE, CaseType.CREANCE, CaseType.RAPPEL
        ));

        // Add more type-step mappings for different case types
        mappings.put(CaseType.TRAVAUX_TIERS, Arrays.asList(
                CaseType.CONTROLE_TRAVAUX
        ));

        mappings.put(CaseType.LIEN_NROPM, Arrays.asList(
                CaseType.CONTROLE_TRAVAUX_LIEN_NROPM
        ));

        mappings.put(CaseType.DESATURATIONS_COUPLEURS, Arrays.asList(
                CaseType.CONTROLE_TRAVAUX_DESATURATIONS_COUPLEURS
        ));

        return Collections.unmodifiableMap(mappings);
    }

    private static Map<String, Duration> initResponseSlaConfigs() {
        Map<String, Duration> slaConfigs = new HashMap<>();

        // Format: "TYPE:STEP:PRIORITY" -> Duration
        // P1 (high priority) tickets
        slaConfigs.put("GRAFIC:GEORESO:P1", Duration.ofHours(2));
        slaConfigs.put("GRAFIC:GEORESO_CABLE:P1", Duration.ofHours(2));
        slaConfigs.put("BRAME:BRAME_ETUDE:P1", Duration.ofHours(1));
        slaConfigs.put("BRAME:BRAME_RACCORDEMENT:P1", Duration.ofHours(1));

        // P2 (medium priority) tickets
        slaConfigs.put("GRAFIC:GEORESO:P2", Duration.ofHours(4));
        slaConfigs.put("GRAFIC:GEORESO_CABLE:P2", Duration.ofHours(4));
        slaConfigs.put("BRAME:BRAME_ETUDE:P2", Duration.ofHours(3));
        slaConfigs.put("BRAME:BRAME_RACCORDEMENT:P2", Duration.ofHours(3));

        // P3 (lower priority) tickets
        slaConfigs.put("GRAFIC:GEORESO:P3", Duration.ofHours(8));
        slaConfigs.put("GRAFIC:GEORESO_CABLE:P3", Duration.ofHours(8));
        slaConfigs.put("BRAME:BRAME_ETUDE:P3", Duration.ofHours(6));
        slaConfigs.put("BRAME:BRAME_RACCORDEMENT:P3", Duration.ofHours(6));

        // Default fallback durations if specific combination not found
        slaConfigs.put("DEFAULT:P1", Duration.ofHours(2));
        slaConfigs.put("DEFAULT:P2", Duration.ofHours(4));
        slaConfigs.put("DEFAULT:P3", Duration.ofHours(8));
        slaConfigs.put("DEFAULT:P4", Duration.ofHours(12));
        slaConfigs.put("DEFAULT:P5", Duration.ofHours(24));

        return Collections.unmodifiableMap(slaConfigs);
    }

    private static Map<String, Duration> initResolutionSlaConfigs() {
        Map<String, Duration> slaConfigs = new HashMap<>();

        // Format: "TYPE:STEP:PRIORITY" -> Duration
        // P1 (high priority) tickets
        slaConfigs.put("GRAFIC:GEORESO:P1", Duration.ofHours(8));
        slaConfigs.put("GRAFIC:GEORESO_CABLE:P1", Duration.ofHours(8));
        slaConfigs.put("BRAME:BRAME_ETUDE:P1", Duration.ofHours(4));
        slaConfigs.put("BRAME:BRAME_RACCORDEMENT:P1", Duration.ofHours(8));

        // P2 (medium priority) tickets
        slaConfigs.put("GRAFIC:GEORESO:P2", Duration.ofHours(16));
        slaConfigs.put("GRAFIC:GEORESO_CABLE:P2", Duration.ofHours(16));
        slaConfigs.put("BRAME:BRAME_ETUDE:P2", Duration.ofHours(12));
        slaConfigs.put("BRAME:BRAME_RACCORDEMENT:P2", Duration.ofHours(16));

        // P3 (lower priority) tickets
        slaConfigs.put("GRAFIC:GEORESO:P3", Duration.ofHours(24));
        slaConfigs.put("GRAFIC:GEORESO_CABLE:P3", Duration.ofHours(24));
        slaConfigs.put("BRAME:BRAME_ETUDE:P3", Duration.ofHours(24));
        slaConfigs.put("BRAME:BRAME_RACCORDEMENT:P3", Duration.ofHours(24));

        // Default fallback durations if specific combination not found
        slaConfigs.put("DEFAULT:P1", Duration.ofHours(8));
        slaConfigs.put("DEFAULT:P2", Duration.ofHours(16));
        slaConfigs.put("DEFAULT:P3", Duration.ofDays(1));
        slaConfigs.put("DEFAULT:P4", Duration.ofDays(2));
        slaConfigs.put("DEFAULT:P5", Duration.ofDays(5));

        return Collections.unmodifiableMap(slaConfigs);
    }

    @Override
    public Ticket createTicket(Ticket ticket) throws EntityCreationException {
        try {
            // Validate type-step combination
            if (ticket.getType() != null && ticket.getStep() != null &&
                    !isValidTypeStepCombination(ticket.getType(), ticket.getStep())) {
                throw new EntityCreationException("Ticket",
                        "Invalid type-step combination: " + ticket.getType() + " cannot have step " + ticket.getStep());
            }

            LocalDateTime now = LocalDateTime.now();
            ticket.setCreatedAt(now);

            // Calculate and set SLAs
            if (ticket.getType() != null && ticket.getStep() != null && ticket.getPriority() != null) {
                ticket.setResponseSla(calculateResponseSLA(ticket.getType(), ticket.getStep(), ticket.getPriority()));
                ticket.setResolutionSla(calculateResolutionSLA(ticket.getType(), ticket.getStep(), ticket.getPriority()));
            }

            return ticketRepository.save(ticket);
        } catch (Exception e) {
            throw new EntityCreationException("Ticket", "Failed to create ticket", e);
        }
    }

    @Override
    public Ticket getTicketById(UUID ticketId) {
        return ticketRepository.findById(ticketId).orElse(null);
    }

    @Override
    public List<Ticket> getAllTickets(Ticket.Status status, Ticket.Priority priority, String issuedBy, String acteTraitement) {
        try {
            Specification<Ticket> spec = Specification.where(null);

            if (status != null) {
                spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
            }
            if (priority != null) {
                spec = spec.and((root, query, cb) -> cb.equal(root.get("priority"), priority));
            }
            if (issuedBy != null && !issuedBy.isEmpty()) {
                spec = spec.and((root, query, cb) -> cb.equal(root.get("issuedBy"), issuedBy));
            }
            if (acteTraitement != null && !acteTraitement.isEmpty()) {
                // Use path traversal for the foreign key rather than trying to load the full entity
                spec = spec.and((root, query, cb) -> cb.equal(root.get("acteTraitement").get("idacte"), acteTraitement));
            }

            return ticketRepository.findAll(spec);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public List<Ticket> searchTickets(Ticket.Status status, Ticket.Priority priority, String issuedBy,
                                      String acteTraitement, CaseType type, CaseType step, String subStatus) {
        try {
            Specification<Ticket> spec = Specification.where(null);

            if (status != null) {
                spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
            }
            if (priority != null) {
                spec = spec.and((root, query, cb) -> cb.equal(root.get("priority"), priority));
            }
            if (issuedBy != null && !issuedBy.isEmpty()) {
                spec = spec.and((root, query, cb) -> cb.equal(root.get("issuedBy"), issuedBy));
            }
            if (acteTraitement != null && !acteTraitement.isEmpty()) {
                // Use path traversal for the foreign key rather than trying to load the full entity
                spec = spec.and((root, query, cb) -> cb.equal(root.get("acteTraitement").get("idacte"), acteTraitement));
            }
            if (type != null) {
                spec = spec.and((root, query, cb) -> cb.equal(root.get("type"), type));
            }
            if (step != null) {
                spec = spec.and((root, query, cb) -> cb.equal(root.get("step"), step));
            }
            if (subStatus != null && !subStatus.isEmpty()) {
                spec = spec.and((root, query, cb) -> cb.equal(root.get("subStatus"), subStatus));
            }

            return ticketRepository.findAll(spec);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public Ticket updateTicketStatus(UUID ticketId, Ticket.Status status) throws EntityCreationException {
        Ticket ticket = getTicketById(ticketId);
        if (ticket == null) {
            throw new EntityCreationException("Ticket", "Ticket not found");
        }
        ticket.setStatus(status);
        ticket.setUpdatedAt(LocalDateTime.now());
        return saveTicket(ticket);
    }

    @Override
    public Ticket updateTicket(UUID ticketId, TicketUpdateDTO ticketRequest) throws EntityCreationException {
        Ticket ticket = getTicketById(ticketId);
        if (ticket == null) {
            throw new EntityCreationException("Ticket", "Ticket not found");
        }

        // Validate type-step combination if both are being updated
        if (ticketRequest.getType() != null && ticketRequest.getStep() != null) {
            if (!isValidTypeStepCombination(ticketRequest.getType(), ticketRequest.getStep())) {
                throw new EntityCreationException("Ticket", "Invalid type-step combination: " +
                        ticketRequest.getType() + " cannot have step " + ticketRequest.getStep());
            }
        } else if (ticketRequest.getType() != null && ticket.getStep() != null) {
            // Type is being updated but step remains the same
            if (!isValidTypeStepCombination(ticketRequest.getType(), ticket.getStep())) {
                throw new EntityCreationException("Ticket", "Invalid type-step combination: " +
                        ticketRequest.getType() + " cannot have step " + ticket.getStep());
            }
        } else if (ticket.getType() != null && ticketRequest.getStep() != null) {
            // Step is being updated but type remains the same
            if (!isValidTypeStepCombination(ticket.getType(), ticketRequest.getStep())) {
                throw new EntityCreationException("Ticket", "Invalid type-step combination: " +
                        ticket.getType() + " cannot have step " + ticketRequest.getStep());
            }
        }

        // Update only if provided fields are not null or empty
        if (ticketRequest.getSubject() != null && !ticketRequest.getSubject().trim().isEmpty()) {
            ticket.setSubject(ticketRequest.getSubject());
        }
        if (ticketRequest.getDescription() != null && !ticketRequest.getDescription().trim().isEmpty()) {
            ticket.setDescription(ticketRequest.getDescription());
        }
        if (ticketRequest.getType() != null) {
            ticket.setType(ticketRequest.getType());
        }
        if (ticketRequest.getStep() != null) {
            ticket.setStep(ticketRequest.getStep());
        }
        if (ticketRequest.getSubStatus() != null) {
            ticket.setSubStatus(ticketRequest.getSubStatus());
        }
        if (ticketRequest.getPriority() != null) {
            ticket.setPriority(ticketRequest.getPriority());
        }
        if (ticketRequest.getStatus() != null) {
            ticket.setStatus(ticketRequest.getStatus());
        }
        if (ticketRequest.getIssuedBy() != null && !ticketRequest.getIssuedBy().trim().isEmpty()) {
            ticket.setIssuedBy(ticketRequest.getIssuedBy());
        }

        // Recalculate SLAs if type, step, or priority has changed
        if ((ticketRequest.getType() != null || ticketRequest.getStep() != null || ticketRequest.getPriority() != null)
                && ticket.getType() != null && ticket.getStep() != null && ticket.getPriority() != null) {
            ticket.setResponseSla(calculateResponseSLA(ticket.getType(), ticket.getStep(), ticket.getPriority()));
            ticket.setResolutionSla(calculateResolutionSLA(ticket.getType(), ticket.getStep(), ticket.getPriority()));
        }

        ticket.setUpdatedAt(LocalDateTime.now());
        return saveTicket(ticket);
    }

    @Override
    public void deleteTicket(UUID ticketId) throws EntityCreationException {
        Ticket ticket = getTicketById(ticketId);
        if (ticket == null) {
            throw new EntityCreationException("Ticket", "Ticket not found");
        }
        ticketRepository.delete(ticket);
    }

    private Ticket saveTicket(Ticket ticket) throws EntityCreationException {
        try {
            return ticketRepository.save(ticket);
        } catch (Exception e) {
            throw new EntityCreationException("Ticket", "Failed to save ticket", e);
        }
    }

    @Override
    public List<TicketCount> getTicketCountByStatus() {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
            Root<Ticket> root = query.from(Ticket.class);

            query.multiselect(
                    root.get("status"),
                    cb.count(root)
            )
                    .groupBy(root.get("status"));

            List<Object[]> results = entityManager.createQuery(query).getResultList();

            return results.stream()
                    .map(result -> new TicketCount(result[0], ((Number) result[1]).longValue()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // Log error and return empty result rather than throwing exception
            e.printStackTrace();
            return Arrays.stream(Ticket.Status.values())
                    .map(status -> new TicketCount(status, 0L))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<TicketCount> getTicketCountByType() {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
            Root<Ticket> root = query.from(Ticket.class);

            query.multiselect(
                    root.get("type"),
                    cb.count(root)
            )
                    .groupBy(root.get("type"));

            List<Object[]> results = entityManager.createQuery(query).getResultList();

            return results.stream()
                    .map(result -> new TicketCount(result[0], ((Number) result[1]).longValue()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // Log error and return empty result
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public List<TicketCount> getTicketCountByStep() {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
            Root<Ticket> root = query.from(Ticket.class);

            query.multiselect(
                    root.get("step"),
                    cb.count(root)
            )
                    .groupBy(root.get("step"));

            List<Object[]> results = entityManager.createQuery(query).getResultList();

            return results.stream()
                    .map(result -> new TicketCount(result[0], ((Number) result[1]).longValue()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // Log error and return empty result
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public List<TicketCount> getTicketCountBySubStatus() {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
            Root<Ticket> root = query.from(Ticket.class);

            query.multiselect(
                    root.get("subStatus"),
                    cb.count(root)
            )
                    .groupBy(root.get("subStatus"));

            List<Object[]> results = entityManager.createQuery(query).getResultList();

            return results.stream()
                    .map(result -> new TicketCount(result[0], ((Number) result[1]).longValue()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // Log error and return empty result
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public Map<String, Long> getTicketStatsByPeriod(String period) {
        Map<String, Long> stats = new HashMap<>();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<Ticket> root = query.from(Ticket.class);

        LocalDateTime startDate;
        LocalDateTime now = LocalDateTime.now();

        switch (period.toLowerCase()) {
            case "daily":
                startDate = now.truncatedTo(ChronoUnit.DAYS);
                break;
            case "weekly":
                startDate = now.minusWeeks(1);
                break;
            case "monthly":
                startDate = now.minusMonths(1);
                break;
            case "quarterly":
                startDate = now.minusMonths(3);
                break;
            case "yearly":
                startDate = now.minusYears(1);
                break;
            default:
                startDate = now.minusDays(7); // Default to weekly
        }

        // Total tickets created in period
        query.select(cb.array(cb.literal("created"), cb.count(root)))
                .where(cb.greaterThanOrEqualTo(root.get("createdAt"), startDate));
        Object[] created = entityManager.createQuery(query).getSingleResult();
        stats.put("created", (Long) created[1]);

        // Total tickets resolved in period
        query = cb.createQuery(Object[].class);
        root = query.from(Ticket.class);
        query.select(cb.array(cb.literal("resolved"), cb.count(root)))
                .where(
                        cb.and(
                                cb.equal(root.get("status"), Ticket.Status.RESOLVED),
                                cb.greaterThanOrEqualTo(root.get("updatedAt"), startDate)
                        )
                );
        Object[] resolved = entityManager.createQuery(query).getSingleResult();
        stats.put("resolved", (Long) resolved[1]);

        // Total tickets by type
        for (CaseType type : CaseType.values()) {
            query = cb.createQuery(Object[].class);
            root = query.from(Ticket.class);
            query.select(cb.array(cb.literal(type.name()), cb.count(root)))
                    .where(
                            cb.and(
                                    cb.equal(root.get("type"), type),
                                    cb.greaterThanOrEqualTo(root.get("createdAt"), startDate)
                            )
                    );
            try {
                Object[] typeCount = entityManager.createQuery(query).getSingleResult();
                stats.put("type_" + type.name(), (Long) typeCount[1]);
            } catch (Exception e) {
                stats.put("type_" + type.name(), 0L);
            }
        }

        return stats;
    }

    @Override
    public Duration calculateResponseSLA(CaseType type, CaseType step, Ticket.Priority priority) {
        if (type == null || step == null || priority == null) {
            return Duration.ofHours(24); // Default fallback
        }

        String key = type.name() + ":" + step.name() + ":" + priority.name();
        if (RESPONSE_SLA_CONFIGS.containsKey(key)) {
            return RESPONSE_SLA_CONFIGS.get(key);
        }

        // Try fallback to type:priority
        key = type.name() + ":" + priority.name();
        if (RESPONSE_SLA_CONFIGS.containsKey(key)) {
            return RESPONSE_SLA_CONFIGS.get(key);
        }

        // Default based only on priority
        key = "DEFAULT:" + priority.name();
        return RESPONSE_SLA_CONFIGS.getOrDefault(key, Duration.ofHours(24));
    }

    @Override
    public Duration calculateResolutionSLA(CaseType type, CaseType step, Ticket.Priority priority) {
        if (type == null || step == null || priority == null) {
            return Duration.ofDays(5); // Default fallback
        }

        String key = type.name() + ":" + step.name() + ":" + priority.name();
        if (RESOLUTION_SLA_CONFIGS.containsKey(key)) {
            return RESOLUTION_SLA_CONFIGS.get(key);
        }

        // Try fallback to type:priority
        key = type.name() + ":" + priority.name();
        if (RESOLUTION_SLA_CONFIGS.containsKey(key)) {
            return RESOLUTION_SLA_CONFIGS.get(key);
        }

        // Default based only on priority
        key = "DEFAULT:" + priority.name();
        return RESOLUTION_SLA_CONFIGS.getOrDefault(key, Duration.ofDays(5));
    }

    @Override
    public boolean isValidTypeStepCombination(CaseType type, CaseType step) {
        if (type == null || step == null) {
            return false;
        }

        List<CaseType> validSteps = VALID_TYPE_STEP_MAPPINGS.get(type);

        // If we don't have explicit mappings for this type, allow any step
        if (validSteps == null) {
            return true;
        }

        return validSteps.contains(step);
    }

    @Override
    public List<CaseType> getValidStepsForType(CaseType type) {
        if (type == null) {
            return Collections.emptyList();
        }

        return VALID_TYPE_STEP_MAPPINGS.getOrDefault(type, Collections.emptyList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true) // Use read-only transaction for query operations
    public Page<Ticket> getTickets(
            int page,
            int size,
            String sortField,
            String sortDir,
            String q,
            Ticket.Status status,
            Ticket.Priority priority) {

        // Validate and map sort field
        String actualSortField = mapAndValidateSortField(sortField);

        // Validate sort direction
        Sort.Direction direction = validateSortDirection(sortDir);

        // Create Pageable object
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, actualSortField));

        // Create Specification for filtering
        Specification<Ticket> spec = TicketSpecification.filterBy(q, status, priority);

        // Fetch data from repository
        return ticketRepository.findAll(spec, pageable);
    }

    /**
     * Maps the user-provided sort field alias (like "timestamp" or "title")
     * to the actual entity field name and validates if it's allowed.
     *
     * @param sortField The sort field provided by the user.
     * @return The actual entity field name to sort by.
     * @throws InvalidSortParameterException if the sort field is not allowed.
     */
    private String mapAndValidateSortField(String sortField) {
        String actualField;
        switch (sortField.toLowerCase()) {
            case "timestamp":
                actualField = "createdAt";
                break;
            case "title":
                actualField = "subject";
                break;
            case "priority":
                actualField = "priority";
                break;
            case "status":
                actualField = "status";
                break;
            default:
                // Allow direct field names if they are in the allowed list
                if (ALLOWED_SORT_FIELDS.contains(sortField)) {
                    actualField = sortField;
                } else {
                    throw new InvalidSortParameterException(
                            "Invalid sort field: '" + sortField + "'. Allowed fields are: " +
                                    String.join(", ", "timestamp", "title", "priority", "status") + "."
                    );
                }
        }
        // Double-check if the mapped field is in the allowed list (should always be true here)
        if (!ALLOWED_SORT_FIELDS.contains(actualField)) {
            throw new InvalidSortParameterException("Internal error: Mapped sort field '" + actualField + "' is not allowed.");
        }
        return actualField;
    }

    /**
     * Validates the sort direction string.
     *
     * @param sortDir The sort direction string ("asc" or "desc", case-insensitive).
     * @return The corresponding Sort.Direction enum.
     * @throws InvalidSortParameterException if the direction is invalid.
     */
    private Sort.Direction validateSortDirection(String sortDir) {
        if ("asc".equalsIgnoreCase(sortDir)) {
            return Sort.Direction.ASC;
        } else if ("desc".equalsIgnoreCase(sortDir)) {
            return Sort.Direction.DESC;
        } else {
            throw new InvalidSortParameterException(
                    "Invalid sort direction: '" + sortDir + "'. Allowed directions are: 'asc', 'desc'."
            );
        }
    }
}
