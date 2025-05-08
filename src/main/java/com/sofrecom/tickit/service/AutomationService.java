package com.sofrecom.tickit.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.sofrecom.tickit.config.AutomationProperties;
import com.sofrecom.tickit.dto.*;
import com.sofrecom.tickit.exception.EntityCreationException;
import com.sofrecom.tickit.listener.SlaBreachEvent;
import com.sofrecom.tickit.listener.TicketPriorityEscalatedEvent;
import com.sofrecom.tickit.listener.DueDateApproachingEvent;
import com.sofrecom.tickit.listener.ActivityStalledEvent;

import com.sofrecom.tickit.mapper.TicketMapper;
import com.sofrecom.tickit.model.Case;
import com.sofrecom.tickit.model.LedgerEntry;
import com.sofrecom.tickit.model.Ticket;
import com.sofrecom.tickit.model.TicketActivity;
import com.sofrecom.tickit.repository.TicketActivityRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class AutomationService {
    private final CaseService caseService;
    private final TicketService ticketService;
    private final LedgerService ledgerService;
    private final TicketMapper ticketMapper;
    private final AutomationProperties properties;
    private final ApplicationEventPublisher eventPublisher;
    private final TicketActivityRepository ticketActivityRepository;
    
    /**
     * Processes overdue cases by checking treatment acts via the blockchain ledger.
     * If a case has an overdue treatment act, changes its status to BLOCKED and creates a ticket.
     */
    public void processOverdueCases() {
        log.info("Starting automatic detection of overdue treatment acts");
        
        // Find all non-closed cases
        List<CaseResponse> activeCases = caseService.getAllCases().stream()
            .filter(caseResponse -> caseResponse.getStatus() != Case.Status.CLOSED && 
                                    caseResponse.getStatus() != Case.Status.CANCELED)
            .collect(Collectors.toList());
        
        log.info("Found {} active cases to check", activeCases.size());
        
        LocalDateTime now = LocalDateTime.now();
        int processedCount = 0;
        int overdueCount = 0;
        
        for (CaseResponse caseResponse : activeCases) {
            try {
                // Get ledger entries for treatment acts of this case (ONLY CREATE actions)
                List<LedgerEntry> ledgerEntries = ledgerService.getLedgerForCase(
                    caseResponse.getId().toString(), "TREATMENT_ACT").stream()
                    .filter(entry -> entry.getActionType() == LedgerEntry.ActionType.CREATE)
                    .collect(Collectors.toList());
                
                if (ledgerEntries.isEmpty()) {
                    continue; // Skip cases with no treatment acts
                }
                
                // Find the latest treatment act entry
                LedgerEntry latestEntry = ledgerEntries.stream()
                    .sorted(Comparator.comparing(LedgerEntry::getTimestamp).reversed())
                    .findFirst()
                    .orElse(null);
                
                if (latestEntry == null) {
                    continue;
                }
                
                // Extract the creation date and duration
                LocalDateTime createdAt = extractCreationDate(latestEntry);
                if (createdAt == null) {
                    log.warn("Could not determine creation date for treatment act in case {}", caseResponse.getId());
                    continue;
                }
                
                int durationDays = extractDuration(latestEntry.getDataSnapshot());
                
                // Calculate the due date
                LocalDateTime dueDate = createdAt.plusDays(durationDays);
                
                // Check if it's overdue and the case is not already blocked
                if (now.isAfter(dueDate) && caseResponse.getStatus() != Case.Status.BLOCKED) {
                    log.info("Case {} is overdue. Last treatment act was due on {}", 
                            caseResponse.getId(), dueDate);
                    
                    // Update case status to BLOCKED
                    blockCase(caseResponse.getId());
                    
                    // Create a ticket if configured to do so
                    if (properties.isCreateTicketsForOverdueCases()) {
                        createTicketForOverdueCase(caseResponse, latestEntry, dueDate);
                    }
                    
                    overdueCount++;
                }
                
                processedCount++;
            } catch (Exception e) {
                log.error("Error processing case {}: {}", caseResponse.getId(), e.getMessage(), e);
            }
        }
        
        log.info("Completed overdue case detection. Processed: {}, Overdue cases found: {}", 
                processedCount, overdueCount);
    }
    
    /**
     * Processes tickets that are approaching their resolution SLA breach.
     * Increases the priority of tickets where createdAt + resolutionSLA > currentDate.
     */
    public void processResolutionSlaBreaches() {
        log.info("Starting resolution SLA breach detection");
        
        // Get all in-progress tickets
        List<Ticket> inProgressTickets = ticketService.getAllTickets(Ticket.Status.IN_PROGRESS, null, null, null);
        
        LocalDateTime now = LocalDateTime.now();
        int escalatedCount = 0;
        
        for (Ticket ticket : inProgressTickets) {
            try {
                // Skip tickets without resolution SLA
                if (ticket.getResolutionSla() == null || ticket.getCreatedAt() == null) {
                    continue;
                }
                
                // Calculate SLA deadline
                LocalDateTime slaDeadline = ticket.getCreatedAt().plus(ticket.getResolutionSla());
                
                // Check if deadline has passed and ticket is not already at highest priority
                if (now.isAfter(slaDeadline) && ticket.getPriority() != Ticket.Priority.P1) {
                    log.info("Ticket {} has breached resolution SLA. Escalating priority from {} to {}", 
                            ticket.getId(), ticket.getPriority(), escalateTicketPriority(ticket.getPriority()));
                    
                    // Store old priority for the event
                    Ticket.Priority oldPriority = ticket.getPriority();
                    Ticket.Priority newPriority = escalateTicketPriority(oldPriority);
                    
                    // Update ticket priority
                    escalateTicketPriority(ticket);
                    escalatedCount++;
                    
                    // Publish SLA breach event
                    eventPublisher.publishEvent(new SlaBreachEvent(ticket, "resolution"));
                    
                    // Publish priority escalated event
                    eventPublisher.publishEvent(new TicketPriorityEscalatedEvent(ticket, oldPriority, newPriority));
                }
            } catch (Exception e) {
                log.error("Error processing ticket {} for resolution SLA: {}", 
                        ticket.getId(), e.getMessage(), e);
            }
        }
        
        log.info("Completed resolution SLA check. Tickets escalated: {}", escalatedCount);
    }
    
    /**
     * Processes tickets that are approaching their response SLA breach.
     * Increases the priority of tickets where createdAt + responseSLA > currentDate.
     */
    public void processResponseSlaBreaches() {
        log.info("Starting response SLA breach detection");
        
        // Get all open tickets
        List<Ticket> openTickets = ticketService.getAllTickets(Ticket.Status.OPEN, null, null, null);
        
        LocalDateTime now = LocalDateTime.now();
        int escalatedCount = 0;
        
        for (Ticket ticket : openTickets) {
            try {
                // Skip tickets without response SLA
                if (ticket.getResponseSla() == null || ticket.getCreatedAt() == null) {
                    continue;
                }
                
                // Calculate SLA deadline
                LocalDateTime slaDeadline = ticket.getCreatedAt().plus(ticket.getResponseSla());
                
                // Check if deadline has passed and ticket is not already at highest priority
                if (now.isAfter(slaDeadline) && ticket.getPriority() != Ticket.Priority.P1) {
                    log.info("Ticket {} has breached response SLA. Escalating priority from {} to {}", 
                            ticket.getId(), ticket.getPriority(), escalateTicketPriority(ticket.getPriority()));
                    
                    // Store old priority for the event
                    Ticket.Priority oldPriority = ticket.getPriority();
                    Ticket.Priority newPriority = escalateTicketPriority(oldPriority);
                    
                    // Update ticket priority
                    escalateTicketPriority(ticket);
                    escalatedCount++;
                    
                    // Publish SLA breach event
                    eventPublisher.publishEvent(new SlaBreachEvent(ticket, "response"));
                    
                    // Publish priority escalated event
                    eventPublisher.publishEvent(new TicketPriorityEscalatedEvent(ticket, oldPriority, newPriority));
                }
            } catch (Exception e) {
                log.error("Error processing ticket {} for response SLA: {}", 
                        ticket.getId(), e.getMessage(), e);
            }
        }
        
        log.info("Completed response SLA check. Tickets escalated: {}", escalatedCount);
    }
    
    /**
     * Processes approaching due dates for treatment acts.
     * Sends notifications for acts that are due within configured days.
     */
    public void processApproachingDueDates() {
        log.info("Starting due date approaching check");
        
        // Find all active cases
        List<CaseResponse> activeCases = caseService.getAllCases().stream()
            .filter(caseResponse -> caseResponse.getStatus() == Case.Status.ONGOING)
            .collect(Collectors.toList());
        
        log.info("Found {} active cases to check", activeCases.size());
        
        LocalDateTime now = LocalDateTime.now();
        int notifiedCount = 0;
        
        // Configure warning threshold (default: 4 days)
        int warningThresholdDays = 4;
        
        for (CaseResponse caseResponse : activeCases) {
            try {
                // Get ledger entries for treatment acts of this case
                List<LedgerEntry> ledgerEntries = ledgerService.getLedgerForCase(
                    caseResponse.getId().toString(), "TREATMENT_ACT").stream()
                    .filter(entry -> entry.getActionType() == LedgerEntry.ActionType.CREATE)
                    .collect(Collectors.toList());
                
                if (ledgerEntries.isEmpty()) {
                    continue; // Skip cases with no treatment acts
                }
                
                // Find the latest treatment act entry
                LedgerEntry latestEntry = ledgerEntries.stream()
                    .sorted(Comparator.comparing(LedgerEntry::getTimestamp).reversed())
                    .findFirst()
                    .orElse(null);
                
                if (latestEntry == null) {
                    continue;
                }
                
                // Extract the creation date and duration
                LocalDateTime createdAt = extractCreationDate(latestEntry);
                if (createdAt == null) {
                    continue;
                }
                
                int durationDays = extractDuration(latestEntry.getDataSnapshot());
                
                // Calculate the due date
                LocalDateTime dueDate = createdAt.plusDays(durationDays);
                
                // Calculate days remaining until due date
                long daysRemaining = java.time.Duration.between(now, dueDate).toDays();
                
                // Check if due date is approaching (within warning threshold)
                if (daysRemaining > 0 && daysRemaining <= warningThresholdDays) {
                    log.info("Due date approaching for case {}: {} days remaining", 
                            caseResponse.getId(), daysRemaining);
                    
                    // Publish due date approaching event
                    eventPublisher.publishEvent(new DueDateApproachingEvent(
                            caseResponse.getId(),
                            caseResponse.getName(),
                            latestEntry.getEntityId(),
                            (int) daysRemaining
                    ));
                    
                    notifiedCount++;
                }
            } catch (Exception e) {
                log.error("Error processing case {} for due date check: {}", 
                        caseResponse.getId(), e.getMessage(), e);
            }
        }
        
        log.info("Completed due date approaching check. Cases with approaching due dates: {}", notifiedCount);
    }
    
    /**
     * Processes activities to find those that have stalled.
     * An activity is considered stalled if it's not completed after a threshold period.
     */
    public void processStalledActivities() {
        log.info("Starting stalled activity check");
        
        // Configure stalled threshold (default: 3 days)
        int stalledThresholdDays = 3;
        
        // Get the cutoff date
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(stalledThresholdDays);
        
        // Find all incomplete activities older than the threshold
        List<TicketActivity> stalledActivities = ticketActivityRepository.findStalledActivities(cutoffDate);
        
        log.info("Found {} potentially stalled activities", stalledActivities.size());
        
        int notifiedCount = 0;
        
        for (TicketActivity activity : stalledActivities) {
            try {
                // Calculate days since last update
                LocalDateTime starTime = activity.getStartTime() != null ? 
                        activity.getStartTime() : activity.getCreatedAt();
                
                
                long daysSinceStart = Duration.between(starTime, LocalDateTime.now()).toDays();
                
                log.info("Activity {} on ticket {} has been stalled for {} days", 
                        activity.getId(), activity.getTicket().getId(), daysSinceStart);
                
                // Publish activity stalled event
                eventPublisher.publishEvent(new ActivityStalledEvent(
                        activity.getTicket().getId(),
                        activity.getId(),
                        activity.getType().name(),
                        activity.getUsername(),
                        (int) daysSinceStart
                ));
                
                notifiedCount++;
            } catch (Exception e) {
                log.error("Error processing stalled activity {}: {}", 
                        activity.getId(), e.getMessage(), e);
            }
        }
        
        log.info("Completed stalled activity check. Notified for {} activities", notifiedCount);
    }
    
    /**
     * Helper method to extract the creation date from a ledger entry.
     * Tries to extract from dataSnapshot first, and falls back to the ledger entry's timestamp
     * if dataSnapshot parsing fails.
     * 
     * @param ledgerEntry The ledger entry to extract creation date from
     * @return The creation date as LocalDateTime, or null if it couldn't be determined
     */
    private LocalDateTime extractCreationDate(LedgerEntry ledgerEntry) {
        // First attempt to extract from dataSnapshot
        LocalDateTime dateFromSnapshot = extractCreationDateFromSnapshot(ledgerEntry.getDataSnapshot());
        
        // If successful, return it
        if (dateFromSnapshot != null) {
            return dateFromSnapshot;
        }
        
        // Otherwise, for CREATE actions, fall back to the ledger entry timestamp
        if (ledgerEntry.getActionType() == LedgerEntry.ActionType.CREATE) {
            log.info("Using ledger entry timestamp as creation date for treatment act {}", 
                    ledgerEntry.getEntityId());
            return ledgerEntry.getTimestamp();
        }
        
        // If all else fails, return null
        return null;
    }
    
    /**
     * Attempts to extract creation date from the dataSnapshot.
     * Handles various date formats found in the system.
     */
    private LocalDateTime extractCreationDateFromSnapshot(JsonNode dataSnapshot) {
        try {
            // Try different date fields based on the structure
            if (dataSnapshot.has("createdAt")) {
                return parseDateTime(dataSnapshot.get("createdAt").asText());
            } else if (dataSnapshot.has("dateReception")) {
                return parseDateTime(dataSnapshot.get("dateReception").asText());
            }
        } catch (Exception e) {
            log.warn("Error extracting creation date: {}", e.getMessage());
        }
        return null;
    }
    
    /**
     * Parses a date string into a LocalDateTime using multiple strategies.
     * 
     * @param dateStr The date string to parse
     * @return A LocalDateTime, or null if parsing failed
     */
    private LocalDateTime parseDateTime(String dateStr) {
        // Try various parsing strategies
        try {
            // 1. Direct LocalDateTime parse (ISO format)
            return LocalDateTime.parse(dateStr);
        } catch (DateTimeParseException e1) {
            try {
                // 2. LocalDate parse and convert to start of day
                return LocalDate.parse(dateStr).atStartOfDay();
            } catch (DateTimeParseException e2) {
                try {
                    // 3. Custom format: yyyy-MM-dd
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    return LocalDate.parse(dateStr, formatter).atStartOfDay();
                } catch (DateTimeParseException e3) {
                    try {
                        // 4. Java's default Date.toString() format: "EEE MMM dd HH:mm:ss zzz yyyy"
                        DateTimeFormatter javaDefaultFormatter = 
                            DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy");
                        return LocalDateTime.parse(dateStr, javaDefaultFormatter);
                    } catch (DateTimeParseException e4) {
                        try {
                            // 5. Last resort: try to parse using java.util.Date and convert
                            @SuppressWarnings("deprecation")
                            Date date = new Date(dateStr);
                            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                        } catch (Exception e5) {
                            // If all parsing attempts fail, log and return null
                            log.warn("Failed to parse date string: '{}' using all available methods", dateStr);
                            return null;
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Helper method to extract the duration from the data snapshot.
     */
    private int extractDuration(JsonNode dataSnapshot) {
        // Default to minimum duration if not specified
        int minimumDuration = properties.getMinimumDurationDays();
        
        if (dataSnapshot != null && dataSnapshot.has("duree")) {
            try {
                int duration = dataSnapshot.get("duree").asInt(minimumDuration);
                return Math.max(duration, minimumDuration);
            } catch (Exception e) {
                log.warn("Error extracting duration: {}", e.getMessage());
            }
        }
        
        return minimumDuration;
    }
    
    /**
     * Updates a case to BLOCKED status.
     */
    private void blockCase(UUID caseId) {
        try {
            CaseUpdate update = new CaseUpdate();
            update.setStatus(Case.Status.BLOCKED);
            caseService.updateCase(caseId, update);
            log.info("Case {} status updated to BLOCKED", caseId);
        } catch (Exception e) {
            log.error("Failed to update case {} status: {}", caseId, e.getMessage(), e);
        }
    }
    
    /**
     * Creates a ticket for an overdue case.
     */
    private void createTicketForOverdueCase(CaseResponse caseResponse, LedgerEntry actEntry, LocalDateTime dueDate) {
        try {
            // Create a ticket request
            TicketRequest ticketRequest = new TicketRequest();
            
            ticketRequest.setSubject("OVERDUE: " + caseResponse.getName());
            
            String description = String.format(
                "Treatment act in case '%s' (ID: %s) is overdue. Due date was: %s. " +
                "Case status has been set to BLOCKED.",
                caseResponse.getName(),
                caseResponse.getId(),
                dueDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            );
            
            ticketRequest.setDescription(description);
            ticketRequest.setPriority(Ticket.Priority.P2); // Default priority for overdue cases
            ticketRequest.setStatus(Ticket.Status.OPEN);
            ticketRequest.setType(caseResponse.getType());
            
            // Additional metadata
            ticketRequest.setIssuedBy("System-Automation"); // Doesn't really matter
            ticketRequest.setActeTraitement(actEntry.getEntityId());
            
            // Create the ticket
            Ticket ticket = ticketMapper.toEntity(ticketRequest);
            ticketService.createTicket(ticket);
            
            log.info("Created ticket for overdue case {}", caseResponse.getId());
        } catch (EntityCreationException e) {
            log.error("Failed to create ticket for overdue case {}: {}", 
                    caseResponse.getId(), e.getMessage(), e);
        }
    }
    
    /**
     * Escalates the priority of a ticket by one level.
     */
    private void escalateTicketPriority(Ticket ticket) {
        try {
            TicketUpdateDTO updateDTO = new TicketUpdateDTO();
            updateDTO.setPriority(escalateTicketPriority(ticket.getPriority()));
            ticketService.updateTicket(ticket.getId(), updateDTO);
        } catch (Exception e) {
            log.error("Failed to update ticket {} priority: {}", 
                    ticket.getId(), e.getMessage(), e);
        }
    }
    
    /**
     * Returns the next higher priority level.
     */
    private Ticket.Priority escalateTicketPriority(Ticket.Priority currentPriority) {
        switch (currentPriority) {
            case P5: return Ticket.Priority.P4;
            case P4: return Ticket.Priority.P3;
            case P3: return Ticket.Priority.P2;
            case P2: return Ticket.Priority.P1;
            default: return currentPriority;  // Already P1 or unknown
        }
    }
}
