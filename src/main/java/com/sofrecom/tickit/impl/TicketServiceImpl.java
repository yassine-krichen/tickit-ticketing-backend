package com.sofrecom.tickit.impl;

import com.sofrecom.tickit.dto.CaseResponse;
import com.sofrecom.tickit.dto.CaseUpdate;
import com.sofrecom.tickit.dto.TicketActivityRequest;
import com.sofrecom.tickit.dto.TicketCount;
import com.sofrecom.tickit.dto.TicketUpdateDTO;
import com.sofrecom.tickit.exception.EntityCreationException;
// import com.sofrecom.tickit.exception.InvalidSlaStrategyException;
import com.sofrecom.tickit.exception.InvalidSortParameterException;
import com.sofrecom.tickit.listener.*;
import com.sofrecom.tickit.model.Acte_traitement;
import com.sofrecom.tickit.model.Case;
import com.sofrecom.tickit.model.CaseType;
import com.sofrecom.tickit.model.Ticket;
import com.sofrecom.tickit.model.TicketActivity;
import com.sofrecom.tickit.repository.TicketRepository;
import com.sofrecom.tickit.repository.TicketSpecification;
import com.sofrecom.tickit.service.CaseService;
import com.sofrecom.tickit.service.TicketActivityService;
import com.sofrecom.tickit.service.TicketService;
import com.sofrecom.tickit.util.AuthUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

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
@Slf4j
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketActivityService ticketActivityService;
    private final CaseService caseService;
    private final ApplicationEventPublisher eventPublisher;
    private final AuthUtils authUtils; // Assuming you have an AuthUtils class for authentication

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

    /**
     * Creates a new ticket and updates the associated case status to BLOCKED.
     *
     * @param ticket The ticket to create
     * @return The created ticket
     * @throws EntityCreationException if there is an error creating the ticket
     */
    @Override
    @Transactional
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
            ticket.setUpdatedAt(now);

            // Calculate and set SLAs
            if (ticket.getType() != null && ticket.getStep() != null && ticket.getPriority() != null) {
                ticket.setResponseSla(calculateResponseSLA(ticket.getType(), ticket.getStep(), ticket.getPriority()));
                ticket.setResolutionSla(calculateResolutionSLA(ticket.getType(), ticket.getStep(), ticket.getPriority()));
            }

            // Save the ticket first to ensure it exists
            Ticket savedTicket = ticketRepository.save(ticket);
            
            // Update the associated case status to BLOCKED if a case is associated
            updateCaseStatusForTicketCreation(savedTicket);
            
            // Publish ticket created event
            log.info("Publishing TicketCreatedEvent for ticket {}", savedTicket.getId());
            eventPublisher.publishEvent(new TicketCreatedEvent(savedTicket));

            //add a ticket activity with open type
            TicketActivityRequest startActivity = new TicketActivityRequest();
            startActivity.setType(TicketActivity.ActivityType.OPEN);
            startActivity.setDescription("Ticket created");
            startActivity.setUsername(authUtils.getUsernameFromFingerprint(ticket.getIssuedBy())); // change wit auth utils TODO
            ticketActivityService.createActivity(savedTicket.getId(), startActivity);
            
            return savedTicket;
        } catch (Exception e) {
            throw new EntityCreationException("Ticket", "Failed to create ticket: " + e.getMessage(), e);
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

    /**
     * Updates the ticket status and manages the associated case status.
     * If the ticket status is changed to RESOLVED, the case status will be updated to ONGOING.
     *
     * @param ticketId The ID of the ticket to update
     * @param status The new status for the ticket
     * @return The updated ticket
     * @throws EntityCreationException if there is an error updating the ticket
     */
    @Override
    @Transactional
    public Ticket updateTicketStatus(UUID ticketId, Ticket.Status status, String userFingerprint) throws EntityCreationException {
        Ticket ticket = getTicketById(ticketId);
        if (ticket == null) {
            throw new EntityCreationException("Ticket", "Ticket not found");
        }
        
        // Store the previous status for comparison
        Ticket.Status previousStatus = ticket.getStatus();
        
        // Skip if status hasn't changed
        if (previousStatus == status) {
            return ticket;
        }
        
        // Update the ticket status
        ticket.setStatus(status);
        ticket.setUpdatedAt(LocalDateTime.now());
        
        // Track status transitions with timestamps (only set if not already set)
        LocalDateTime now = LocalDateTime.now();
        
        if (status == Ticket.Status.IN_PROGRESS && ticket.getInProgressEnteredAt() == null) {
            ticket.setInProgressEnteredAt(now);
            log.info("Ticket {} entered IN_PROGRESS status for the first time", ticketId);
        } else if (status == Ticket.Status.BLOCKED && ticket.getBlockedEnteredAt() == null) {
            ticket.setBlockedEnteredAt(now);
            log.info("Ticket {} entered BLOCKED status for the first time", ticketId);
        } else if (status == Ticket.Status.RESOLVED && ticket.getResolvedAt() == null) {
            ticket.setResolvedAt(now);
            log.info("Ticket {} was resolved at {}", ticketId, now);
        }
        
        Ticket updatedTicket = saveTicket(ticket);
        
        // Rest of the existing code for handling specific status transitions...
        if (status == Ticket.Status.RESOLVED && previousStatus != Ticket.Status.RESOLVED) {
            updateCaseStatusForTicketResolution(updatedTicket);
            // Add debug logging
            log.info("Publishing TicketResolvedEvent for ticket {}", updatedTicket.getId());
            try {
                eventPublisher.publishEvent(new TicketResolvedEvent(updatedTicket));
            } catch (Exception e) {
                log.error("Error publishing TicketResolvedEvent: {}", e.getMessage(), e);
            }
        } else if (status == Ticket.Status.CLOSED && previousStatus != Ticket.Status.CLOSED) {
            // create new ticket activity to be added to the ticket
            TicketActivityRequest actReq = new TicketActivityRequest();
            actReq.setUsername(authUtils.getUsernameFromFingerprint(userFingerprint));
            actReq.setType(TicketActivity.ActivityType.CLOSE);
            actReq.setDescription("Ticket closed");
            ticketActivityService.createActivity(ticketId, actReq);

            eventPublisher.publishEvent(new TicketUpdatedEvent(updatedTicket));
        } else if (status == Ticket.Status.OPEN && previousStatus != Ticket.Status.OPEN) {
            // create new ticket activity to be added to the ticket
            TicketActivityRequest actReq = new TicketActivityRequest();
            actReq.setUsername(authUtils.getUsernameFromFingerprint(userFingerprint));
            actReq.setType(TicketActivity.ActivityType.OPEN);
            actReq.setDescription("Ticket re-opened");
            ticketActivityService.createActivity(ticketId, actReq);

            eventPublisher.publishEvent(new TicketUpdatedEvent(updatedTicket));
        } else {    
            // Publish ticket updated event
            eventPublisher.publishEvent(new TicketUpdatedEvent(updatedTicket));
        }
        
        return updatedTicket;
    }

    @Override
    public Ticket updateTicket(UUID ticketId, TicketUpdateDTO ticketRequest) throws EntityCreationException {
        Ticket ticket = getTicketById(ticketId);
        if (ticket == null) {
            throw new EntityCreationException("Ticket", "Ticket not found");
        }

        // Check for valid type-step combination
        if (ticketRequest.getType() != null && ticketRequest.getStep() != null) {
            if (!isValidTypeStepCombination(ticketRequest.getType(), ticketRequest.getStep())) {
                throw new EntityCreationException("Ticket", "Invalid type-step combination: " +
                        ticketRequest.getType() + " cannot have step " + ticketRequest.getStep());
            }
        } else if (ticket.getType() != null && ticketRequest.getStep() != null) {
            if (!isValidTypeStepCombination(ticket.getType(), ticketRequest.getStep())) {
                throw new EntityCreationException("Ticket", "Invalid type-step combination: " +
                        ticket.getType() + " cannot have step " + ticketRequest.getStep());
            }
        } else if (ticketRequest.getType() != null && ticket.getStep() != null) {
            if (!isValidTypeStepCombination(ticketRequest.getType(), ticket.getStep())) {
                throw new EntityCreationException("Ticket", "Invalid type-step combination: " +
                        ticketRequest.getType() + " cannot have step " + ticket.getStep());
            }
        }

        // Save old priority for comparison
        Ticket.Priority oldPriority = ticket.getPriority();

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
            // ticket.setStatus(ticketRequest.getStatus());
            updateTicketStatus(ticketId, ticketRequest.getStatus(), ticketRequest.getIssuedBy());
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
        Ticket updatedTicket = saveTicket(ticket);
        
        // Check if priority was escalated
        if (ticketRequest.getPriority() != null && ticketRequest.getPriority().ordinal() < oldPriority.ordinal()) {
            eventPublisher.publishEvent(new TicketPriorityEscalatedEvent(updatedTicket, oldPriority, ticketRequest.getPriority()));
        } else {
            // Standard update notification
            eventPublisher.publishEvent(new TicketUpdatedEvent(updatedTicket));
        }
        
        return updatedTicket;
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

    /**
     * Updates the associated case status to BLOCKED when a ticket is created.
     * 
     * @param ticket The newly created ticket
     */
    private void updateCaseStatusForTicketCreation(Ticket ticket) {
        if (ticket.getActeTraitement() == null) {
            log.debug("Ticket has no associated treatment act ID, skipping case update");
            return;
        }
        
        try {
            UUID caseId = resolveTicketCaseId(ticket);
            
            if (caseId != null) {
                log.info("Updating case {} status to BLOCKED due to new ticket creation", caseId);
                // Get case name
                String caseName = "Unknown Case"; // Default fallback name
                try {
                    CaseResponse caseResponse = caseService.getCaseById(caseId);
                    if (caseResponse != null && caseResponse.getName() != null) {
                        caseName = caseResponse.getName();
                    }
                } catch (Exception e) {
                    log.warn("Could not retrieve case name for case {}: {}", caseId, e.getMessage());
                }
                
                // Create a case update DTO and set the status to BLOCKED
                CaseUpdate caseUpdate = new CaseUpdate();
                caseUpdate.setStatus(Case.Status.BLOCKED);
                
                // Use the CaseService to update the case
                caseService.updateCase(caseId, caseUpdate);
                
                // Publish case blocked event
                eventPublisher.publishEvent(new CaseBlockedEvent(caseId, caseName, ticket.getId()));
                
                log.info("Successfully updated case {} status to BLOCKED", caseId);
            }
        } catch (Exception e) {
            // Log the error but don't fail the ticket creation
            log.error("Failed to update case status for ticket {}: {}", ticket.getId(), e.getMessage(), e);
        }
    }
    
    /**
     * Updates the associated case status to ONGOING when a ticket is resolved.
     * Only updates the case if all associated tickets are resolved.
     * 
     * @param ticket The resolved ticket
     */
    private void updateCaseStatusForTicketResolution(Ticket ticket) {
        if (ticket.getActeTraitement() == null) {
            log.debug("Resolved ticket has no associated treatment act ID, skipping case update");
            return;
        }
        
        try {
            // Resolve the case ID from the ticket
            UUID caseId = resolveTicketCaseId(ticket);
            
            if (caseId != null) {
                // Check if all tickets for this case are resolved before updating the case status
                if (areAllCaseTicketsResolved(caseId, ticket.getId())) {
                    log.info("All tickets for case {} are resolved, updating status to ONGOING", caseId);
                    
                    // Create a case update DTO and set the status to ONGOING
                    CaseUpdate caseUpdate = new CaseUpdate();
                    caseUpdate.setStatus(Case.Status.ONGOING);
                    
                    // Use the CaseService to update the case
                    caseService.updateCase(caseId, caseUpdate);
                    
                    log.info("Successfully updated case {} status to ONGOING", caseId);
                } else {
                    log.info("Not all tickets for case {} are resolved, keeping status as BLOCKED", caseId);
                }
            }
        } catch (Exception e) {
            // Log the error but don't fail the ticket resolution
            log.error("Failed to update case status for resolved ticket {}: {}", ticket.getId(), e.getMessage(), e);
        }
    }
    
    /**
     * Resolves the case ID associated with a ticket.
     * This is a helper method that handles the relationship between tickets, treatment acts, and cases.
     * 
     * @param ticket The ticket to resolve the case for
     * @return The associated case ID, or null if no case is associated
     */
    private UUID resolveTicketCaseId(Ticket ticket) {
        if (ticket.getActeTraitement() == null) {
            return null;
        }
        Case associatedCase = ticket.getActeTraitement().getAssociatedCase();
        return associatedCase != null ? associatedCase.getId() : null;
    }
    
    /**
     * Checks if all tickets associated with a case are resolved, excluding a specific ticket.
     * 
     * @param caseId The case ID to check
     * @param currentTicketId The ID of the ticket to exclude from the check (usually the one just resolved)
     * @return true if all tickets (except the excluded one) are resolved, false otherwise
     */
    private boolean areAllCaseTicketsResolved(UUID caseId, UUID currentTicketId) {
        try {
            // Get all active tickets for this case, excluding the current ticket
            Specification<Ticket> spec = (root, query, cb) -> {
                // Join with acteTraitement to access its associatedCase
                Join<Ticket, Acte_traitement> actJoin = root.join("acteTraitement", JoinType.INNER);
                Join<Acte_traitement, Case> caseJoin = actJoin.join("associatedCase", JoinType.INNER);
                
                return cb.and(
                    // Filter by case ID through the correct join path
                    cb.equal(caseJoin.get("id"), caseId),
                    // Exclude already resolved tickets
                    cb.notEqual(root.get("status"), Ticket.Status.RESOLVED),
                    // Exclude the current ticket being processed
                    cb.notEqual(root.get("id"), currentTicketId)
                );
            };
            
            // If we find any non-resolved tickets, return false
            long unresolved = ticketRepository.count(spec);
            return unresolved == 0;
        } catch (Exception e) {
            log.warn("Error checking if all tickets are resolved for case {}: {}", caseId, e.getMessage());
            // Default to false if we can't determine for sure
            return false;
        }
    }

    /**
     * Event listener that handles activity creation events.
     * Updates ticket status to IN_PROGRESS when appropriate activities are created.
     * 
     * @param event The activity created event
     */
    @EventListener
    @Transactional
    public void handleActivityCreatedEvent(ActivityCreatedEvent event) {
        TicketActivity activity = event.getActivity();
        Ticket ticket = activity.getTicket();
        
        // Skip if ticket is already RESOLVED or CLOSED
        if (ticket.getStatus() == Ticket.Status.RESOLVED || 
            ticket.getStatus() == Ticket.Status.CLOSED) {
            return;
        }
        
        // Update ticket status to IN_PROGRESS for work activities
        if (activity.getType() != TicketActivity.ActivityType.OPEN && 
            activity.getType() != TicketActivity.ActivityType.CLOSE && 
            activity.getType() != TicketActivity.ActivityType.UPDATE) {
            
            log.info("Automatically updating ticket {} to IN_PROGRESS due to activity creation", 
                    ticket.getId());
            
            ticket.setStatus(Ticket.Status.IN_PROGRESS);
            ticket.setUpdatedAt(LocalDateTime.now());
            ticketRepository.save(ticket);
            
            // Publish event for the status change
            eventPublisher.publishEvent(new TicketUpdatedEvent(ticket));
        }
    }

    @Override
    public Map<String, Double> getMonthOverMonthComparisons() {
        Map<String, Double> momComparisons = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        
        // Current month boundaries
        LocalDateTime currentMonthStart = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        
        // Previous month boundaries
        LocalDateTime previousMonthStart = currentMonthStart.minusMonths(1);
        LocalDateTime previousMonthEnd = currentMonthStart.minusNanos(1);
        
        log.info("====== CALCULATING MOM COMPARISONS ======");
        log.info("Current month range: {} to {}", currentMonthStart, now);
        log.info("Previous month range: {} to {}", previousMonthStart, previousMonthEnd);
        
        // Get total tickets for current and previous month
        long currentTotal = countTicketsCreatedBetween(currentMonthStart, now);
        long previousTotal = countTicketsCreatedBetween(previousMonthStart, previousMonthEnd);
        
        log.info("TOTAL TICKETS - Current month: {}, Previous month: {}", currentTotal, previousTotal);
        
        // Calculate percentage change for total tickets
        Double totalTicketsChange = calculatePercentageChange(previousTotal, currentTotal);
        momComparisons.put("totalTickets", totalTicketsChange);
        log.info("Total tickets change: {}%", totalTicketsChange);
        
        // IN_PROGRESS tickets - count by timestamp
        long currentInProgress = countTicketsByStatusAndEnteredAtBetween(
            Ticket.Status.IN_PROGRESS, currentMonthStart, now);
        long previousInProgress = countTicketsByStatusAndEnteredAtBetween(
            Ticket.Status.IN_PROGRESS, previousMonthStart, previousMonthEnd);
        
        log.info("IN_PROGRESS TICKETS - Current month: {}, Previous month: {}", 
                currentInProgress, previousInProgress);
        Double inProgressChange = calculatePercentageChange(previousInProgress, currentInProgress);
        momComparisons.put("inProgress", inProgressChange);
        log.info("In-progress tickets change: {}%", inProgressChange);
        
        // BLOCKED tickets
        long currentBlocked = countTicketsByStatusAndEnteredAtBetween(
            Ticket.Status.BLOCKED, currentMonthStart, now);
        long previousBlocked = countTicketsByStatusAndEnteredAtBetween(
            Ticket.Status.BLOCKED, previousMonthStart, previousMonthEnd);
        
        log.info("BLOCKED TICKETS - Current month: {}, Previous month: {}", 
                currentBlocked, previousBlocked);
        Double blockedChange = calculatePercentageChange(previousBlocked, currentBlocked);
        momComparisons.put("blocked", blockedChange);
        log.info("Blocked tickets change: {}%", blockedChange);
        
        // RESOLVED tickets
        long currentResolved = countTicketsByStatusAndEnteredAtBetween(
            Ticket.Status.RESOLVED, currentMonthStart, now);
        long previousResolved = countTicketsByStatusAndEnteredAtBetween(
            Ticket.Status.RESOLVED, previousMonthStart, previousMonthEnd);
        
        // Calculate percentage changes
        momComparisons.put("inProgress", calculatePercentageChange(previousInProgress, currentInProgress));
        momComparisons.put("blocked", calculatePercentageChange(previousBlocked, currentBlocked));
        momComparisons.put("resolved", calculatePercentageChange(previousResolved, currentResolved));
        
        return momComparisons;
    }

    /**
     * Counts tickets created between the specified timestamps.
     */
    private long countTicketsCreatedBetween(LocalDateTime start, LocalDateTime end) {
        long count = ticketRepository.count((root, query, cb) -> 
            cb.and(
                cb.greaterThanOrEqualTo(root.get("createdAt"), start),
                cb.lessThanOrEqualTo(root.get("createdAt"), end)
            )
        );
        log.debug("Count of tickets created between {} and {}: {}", start, end, count);
        return count;
    }

    /**
     * Counts tickets that entered a specific status between the specified timestamps.
     */
    private long countTicketsByStatusAndEnteredAtBetween(Ticket.Status status, LocalDateTime start, LocalDateTime end) {
        // Use the appropriate timestamp field based on the status
        String fieldName = getTimestampFieldForStatus(status);
        
        return ticketRepository.count((root, query, cb) -> 
            cb.and(
                cb.isNotNull(root.get(fieldName)),
                cb.greaterThanOrEqualTo(root.get(fieldName), start),
                cb.lessThanOrEqualTo(root.get(fieldName), end)
            )
        );
    }

    /**
     * Gets the appropriate timestamp field name for a given status.
     */
    private String getTimestampFieldForStatus(Ticket.Status status) {
        switch (status) {
            case IN_PROGRESS:
                return "inProgressEnteredAt";
            case BLOCKED:
                return "blockedEnteredAt";
            case RESOLVED:
                return "resolvedAt";
            default:
                return "createdAt";
        }
    }

    /**
     * Calculates percentage change between previous and current values.
     * Logs the input values and calculation logic.
     */
    private Double calculatePercentageChange(long previous, long current) {
        log.debug("Calculating percentage change: previous={}, current={}", previous, current);
        
        if (previous == 0) {
            if (current == 0) {
                log.debug("Both values are zero, returning 0% change");
                return 0.0;
            }
            log.debug("Previous value is zero, current is non-zero, returning 100% increase");
            return 100.0;
        }
        
        double rawPercentage = ((double) (current - previous) / previous) * 100.0;
        double formattedPercentage = formatPercentage(rawPercentage);
        
        log.debug("Raw percentage change: {}%, formatted: {}%", rawPercentage, formattedPercentage);
        return formattedPercentage;
    }

    // Add this method to format percentages to 1 decimal place
    private Double formatPercentage(double percentage) {
        // Round to 1 decimal place
        return Math.round(percentage * 10.0) / 10.0;
    }

    @Override
    public List<Ticket> findSimilarResolvedTickets(String subject, String type, int limit) {
        // Extract keywords from subject (simple approach)
        String[] keywords = subject.split("\\s+");
        String primaryKeyword = keywords.length > 0 ? keywords[0] : subject;
        
        // Use the updated repository method with proper Status enum and Pageable
        Page<Ticket> ticketPage = ticketRepository.findSimilarResolvedTickets(
            primaryKeyword, 
            Ticket.Status.RESOLVED, 
            type, 
            PageRequest.of(0, limit)
        );
        
        return ticketPage.getContent();
    }
    
    
    

    @Override
    public Map<String, Object> getTicketTrends(int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        List<Object[]> trendData = ticketRepository.getTicketTrends(startDate);
        
        Map<String, Object> trends = new HashMap<>();
        
        // Calculate total tickets from the trend data
        long totalTickets = trendData.stream()
            .mapToLong(row -> (Long) row[1])
            .sum();
        
        trends.put("totalTickets", totalTickets);
        trends.put("dailyData", trendData);
        
        return trends;
    }
    
    @Override
    public Map<String, Double> getResolutionMetrics() {
        Object[] metrics = ticketRepository.getResolutionMetrics(Ticket.Status.RESOLVED.name());
        
        Map<String, Double> result = new HashMap<>();
        
        if (metrics != null && metrics.length >= 3) {
            result.put("averageResolutionHours", (Double) metrics[0]);
            result.put("fastestResolutionHours", (Double) metrics[1]);
            result.put("slowestResolutionHours", (Double) metrics[2]);
        } else {
            // Return default values if no metrics found
            result.put("averageResolutionHours", 0.0);
            result.put("fastestResolutionHours", 0.0);
            result.put("slowestResolutionHours", 0.0);
        }
        
        return result;
    }

}
