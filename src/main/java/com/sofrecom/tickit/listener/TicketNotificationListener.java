package com.sofrecom.tickit.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sofrecom.tickit.model.Collaborateur;
import com.sofrecom.tickit.model.ERole;
import com.sofrecom.tickit.model.NotificationType;
import com.sofrecom.tickit.model.Ticket;
import com.sofrecom.tickit.model.User;
import com.sofrecom.tickit.repository.CollaborateurRepository;
import com.sofrecom.tickit.repository.UserRepository;
import com.sofrecom.tickit.service.NotificationService;
import com.sofrecom.tickit.dto.NotificationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
// import org.springframework.transaction.event.EventListener;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Listener for ticket-related events that creates and sends notifications.
 * Handles various events like ticket creation, updates, and SLA breaches.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TicketNotificationListener {

    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final CollaborateurRepository collaborateurRepository;
    private final ObjectMapper objectMapper;

    /**
     * Handles ticket creation events by creating notifications.
     *
     * @param event The ticket created event
     */
    @Async
    @EventListener
    public void handleTicketCreatedEvent(TicketCreatedEvent event) {
        Ticket ticket = event.getTicket();
        String message = String.format("New ticket created: %s", ticket.getSubject());
        
        // Create payload with ticket details
        JsonNode payload = createTicketPayload(ticket);
        
        // Notify all admins and supervisors
        Set<ERole> roles = createRoleSet(ERole.ROLE_ADMIN, ERole.ROLE_DIRECTION);
        int roleNotifications = notificationService.createForRoles(
                roles,
                NotificationType.TICKET_CREATED,
                message,
                payload
        );
        log.info("Created {} role-based notifications for ticket creation", roleNotifications);
        
        // If the ticket was created by a user (not system), also notify that user
        if (ticket.getIssuedBy() != null && !ticket.getIssuedBy().startsWith("System-")) {
            User user = findUserFromIssuedBy(ticket.getIssuedBy());
            if (user != null) {
                NotificationResponse response = notificationService.createForUser(
                        user.getId(),
                        NotificationType.TICKET_CREATED,
                        message,
                        payload
                );
                log.info("Created ticket creation notification for user {} with ID: {}", 
                        user.getUsername(), response.getId());
            }
        }
    }

    /**
     * Handles ticket resolved events by creating notifications.
     *
     * @param event The ticket resolved event
     */
    @Async
    @EventListener 
    public void handleTicketResolvedEvent(TicketResolvedEvent event) {
        log.info("TicketResolvedEvent received for ticket ID: {}", event.getTicket().getId());
        Ticket ticket = event.getTicket();
        String message = String.format("Ticket resolved: %s", ticket.getSubject());
        // Create payload with ticket details
        JsonNode payload = createTicketPayload(ticket);
        
        // Notify the creator of the ticket
        if (ticket.getIssuedBy() != null) {
            User user = findUserFromIssuedBy(ticket.getIssuedBy());
            if (user != null) {
                NotificationResponse response = notificationService.createForUser(
                        user.getId(),
                        NotificationType.TICKET_RESOLVED,
                        message,
                        payload
                );
                log.info("Created ticket resolved notification for user {} with ID: {}", 
                        user.getUsername(), response.getId());
            }
        }
        
        // Also notify admins
        Set<ERole> roles = createRoleSet(ERole.ROLE_ADMIN);
        int notificationsCount = notificationService.createForRoles(
                roles,
                NotificationType.TICKET_RESOLVED,
                message,
                payload
        );
        log.info("Created {} admin notifications for resolved ticket {}", 
                notificationsCount, ticket.getId());
    }

    /**
     * Handles ticket update events by creating notifications.
     *
     * @param event The ticket updated event
     */
    @Async
    @EventListener
    public void handleTicketUpdatedEvent(TicketUpdatedEvent event) {
        log.info("TicketUpdatedEvent received for ticket ID: {}", event.getTicket().getId());
        Ticket ticket = event.getTicket();
        String message = String.format("Ticket updated: %s", ticket.getSubject());
        
        // Create payload with ticket details
        JsonNode payload = createTicketPayload(ticket);
        
        // Track users who have already been notified to prevent duplicates
        Set<Long> notifiedUserIds = new HashSet<>();
        
        // Notify ticket creator
        if (ticket.getIssuedBy() != null) {
            User user = findUserFromIssuedBy(ticket.getIssuedBy());
            if (user != null) {
                NotificationResponse response = notificationService.createForUser(
                        user.getId(),
                        NotificationType.UPDATE,
                        message,
                        payload
                );
                
                // Add user ID to the set of notified users
                notifiedUserIds.add(user.getId());
                
                log.info("Created ticket update notification for issuer {} with ID: {}", 
                        user.getUsername(), response.getId());
            }
        }
        
        // Notify all users who have worked on this ticket (from activities)
        List<User> activityUsers = findUsersWithActivitiesOnTicket(ticket.getId());
        log.info("Found {} users with activities on ticket {}", activityUsers.size(), ticket.getId());
        
        for (User user : activityUsers) {
            // Skip if we've already sent a notification to this user
            if (notifiedUserIds.contains(user.getId())) {
                log.debug("Skipping duplicate notification for user {} who is already notified", 
                        user.getUsername());
                continue;
            }
            
            NotificationResponse response = notificationService.createForUser(
                    user.getId(),
                    NotificationType.UPDATE,
                    message,
                    payload
            );
            
            // Add to set of notified users
            notifiedUserIds.add(user.getId());
            
            log.info("Created ticket update notification for activity user {} with ID: {}", 
                    user.getUsername(), response.getId());
        }
        
        log.info("Sent ticket update notifications to {} unique users", notifiedUserIds.size());
    }
    
    /**
     * Handles ticket priority escalation events by creating notifications.
     *
     * @param event The ticket priority escalated event
     */
    @Async
    @EventListener
    public void handleTicketPriorityEscalatedEvent(TicketPriorityEscalatedEvent event) {
        Ticket ticket = event.getTicket();
        Ticket.Priority oldPriority = event.getOldPriority();
        Ticket.Priority newPriority = event.getNewPriority();
        
        String message = String.format(
                "Ticket priority escalated from %s to %s: %s", 
                oldPriority, 
                newPriority, 
                ticket.getSubject()
        );
        
        // Create payload with ticket details
        ObjectNode payload = (ObjectNode) createTicketPayload(ticket);
        payload.put("oldPriority", oldPriority.name());
        payload.put("newPriority", newPriority.name());
        
        // Notify direction and admins
        Set<ERole> roles = createRoleSet(ERole.ROLE_ADMIN, ERole.ROLE_DIRECTION);
        int notificationsCount = notificationService.createForRoles(
                roles,
                NotificationType.PRIO_LEVELED,
                message,
                payload
        );
        log.info("Created {} admin/direction notifications for priority escalation", notificationsCount);
        
        // Notify the creator of the ticket
        if (ticket.getIssuedBy() != null) {
            User user = findUserFromIssuedBy(ticket.getIssuedBy());
            if (user != null) {
                NotificationResponse response = notificationService.createForUser(
                        user.getId(),
                        NotificationType.PRIO_LEVELED,
                        message,
                        payload
                );
                log.info("Created priority escalation notification for user {} with ID: {}", 
                        user.getUsername(), response.getId());
            }
        }
    }
    
    /**
     * Handles SLA breach events by creating notifications.
     *
     * @param event The SLA breach event
     */
    @Async
    @EventListener
    public void handleSlaBreachEvent(SlaBreachEvent event) {
        Ticket ticket = event.getTicket();
        String slaType = event.getSlaType(); // "response" or "resolution"
        
        String message = String.format(
                "%s SLA breach for ticket: %s", 
                slaType.substring(0, 1).toUpperCase() + slaType.substring(1), 
                ticket.getSubject()
        );
        
        // Create payload with ticket details
        ObjectNode payload = (ObjectNode) createTicketPayload(ticket);
        payload.put("slaType", slaType);
        
        // Notify admins and supervisors
        Set<ERole> roles = createRoleSet(ERole.ROLE_ADMIN, ERole.ROLE_DIRECTION);
        
        // This call will both persist to database and send real-time notification
        int notificationsCreated = notificationService.createForRoles(
                roles,
                NotificationType.SLA_BREACH,
                message,
                payload
        );
        
        log.info("Created {} SLA breach notifications for ticket {}", notificationsCreated, ticket.getId());
    }
    
    /**
     * Handles activity stalled events by creating notifications.
     *
     * @param event The activity stalled event
     */
    @Async
    @EventListener
    public void handleActivityStalledEvent(ActivityStalledEvent event) {
        UUID ticketId = event.getTicketId();
        Long activityId = event.getActivityId();
        String activityType = event.getActivityType();
        String username = event.getUsername();
        int daysSinceUpdate = event.getDaysSinceUpdate();
        
        String message = String.format(
                "Activity of type %s has been stalled for %d days", 
                activityType, 
                daysSinceUpdate
        );
        
        // Create payload with activity details
        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("ticketId", ticketId.toString());
        payload.put("activityId", activityId);
        payload.put("activityType", activityType);
        payload.put("username", username);
        payload.put("daysSinceUpdate", daysSinceUpdate);
        
        // Notify the user who owns the activity - no need to transform username here
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            NotificationResponse response = notificationService.createForUser(
                    user.getId(),
                    NotificationType.ACTIVITY_STALLED,
                    message,
                    payload
            );
            log.info("Created stalled activity notification for user: {}, notification ID: {}", 
                    username, response.getId());
        } else {
            log.warn("Could not find user with username {} for stalled activity notification", username);
        }
        
        // Also notify direction
        Set<ERole> roles = createRoleSet(ERole.ROLE_DIRECTION);
        int notificationsCreated = notificationService.createForRoles(
                roles,
                NotificationType.ACTIVITY_STALLED,
                message,
                payload
        );
        log.info("Created {} direction notifications for stalled activity", notificationsCreated);
    }
    
    /**
     * Handles case blocked events by creating notifications.
     *
     * @param event The case blocked event
     */
    @Async
    @EventListener
    public void handleCaseBlockedEvent(CaseBlockedEvent event) {
        UUID caseId = event.getCaseId();
        String caseName = event.getCaseName();
        UUID ticketId = event.getTicketId();
        
        String message = String.format("Case %s has been blocked", caseName);
        
        // Create payload with case details
        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("caseId", caseId.toString());
        payload.put("caseName", caseName);
        if (ticketId != null) {
            payload.put("ticketId", ticketId.toString());
        }
        
        // Notify admins and direction
        Set<ERole> roles = createRoleSet(ERole.ROLE_ADMIN, ERole.ROLE_DIRECTION);
        int notificationsCreated = notificationService.createForRoles(
                roles,
                NotificationType.CASE_BLOCKED,
                message,
                payload
        );
        log.info("Created {} notifications for blocked case {}", notificationsCreated, caseId);
    }
    
    /**
     * Handles due date approaching events by creating notifications.
     *
     * @param event The due date approaching event
     */
    @Async
    @EventListener
    public void handleDueDateApproachingEvent(DueDateApproachingEvent event) {
        UUID caseId = event.getCaseId();
        String caseName = event.getCaseName();
        String acteTraitementId = event.getActeTraitementId();
        int daysRemaining = event.getDaysRemaining();
        
        String message = String.format(
                "Due date approaching for treatment act in case %s. %d days remaining.", 
                caseName, 
                daysRemaining
        );
        
        // Create payload with details
        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("caseId", caseId.toString());
        payload.put("caseName", caseName);
        payload.put("acteTraitementId", acteTraitementId);
        payload.put("daysRemaining", daysRemaining);
        
        // Notify users with ROLE_USER (case handlers) and direction
        Set<ERole> roles = createRoleSet(ERole.ROLE_USER, ERole.ROLE_DIRECTION);
        
        int notificationsCreated = notificationService.createForRoles(
                roles,
                NotificationType.DUE_DATE_APPROACHING,
                message,
                payload
        );
        
        log.info("Created {} due date approaching notifications for case {}", 
                notificationsCreated, caseId);
    }

    /**
     * Creates a payload with ticket details for notification messages.
     *
     * @param ticket The ticket to include in the payload
     * @return JsonNode containing ticket details
     */
    private JsonNode createTicketPayload(Ticket ticket) {
        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("id", ticket.getId().toString());
        payload.put("subject", ticket.getSubject());
        payload.put("status", ticket.getStatus().name());
        payload.put("priority", ticket.getPriority().name());
        if (ticket.getType() != null) {
            payload.put("type", ticket.getType().name());
        }
        if (ticket.getStep() != null) {
            payload.put("step", ticket.getStep().name());
        }
        if (ticket.getSubStatus() != null) {
            payload.put("subStatus", ticket.getSubStatus());
        }
        return payload;
    }
    
    /**
     * Finds users who have performed activities on a ticket.
     *
     * @param ticketId The ticket ID
     * @return List of users who have activities on the ticket
     */
    private List<User> findUsersWithActivitiesOnTicket(UUID ticketId) {
        // This is a placeholder - you would need to implement a repository method to find users
        // who have worked on activities for a given ticket
        return userRepository.findUsersByTicketActivities(ticketId);
    }

    /**
     * Creates a set of roles from provided role enumeration values.
     *
     * @param roles The roles to include in the set
     * @return Set of role enumerations
     */
    private Set<ERole> createRoleSet(ERole... roles) {
        Set<ERole> roleSet = new HashSet<>();
        for (ERole role : roles) {
            roleSet.add(role);
        }
        return roleSet;
    }
    
    /**
     * Parses the issuedBy field (format: lastname:firstname:role:team) to find the corresponding User.
     * First finds the Collaborateur by name and then gets the User by CUID.
     *
     * @param issuedBy The issuedBy string from ticket
     * @return User entity or null if not found
     */
    private User findUserFromIssuedBy(String issuedBy) {
        if (!StringUtils.hasText(issuedBy)) {
            log.warn("Empty issuedBy field, cannot find user");
            return null;
        }
        
        log.debug("Finding user from issuedBy: {}", issuedBy);
        
        try {
            // Parse the issuedBy string which has format: lastname.firstname.role.team
            // NOTE: I hate my life, after 2 hours of debugging u need \\ to escape the dot in regex
            // Split the string by '.' to get the parts
            String[] parts = issuedBy.split("\\.");
            // log the parts
            for (String part : parts) {
                log.warn("Part: {}", part);
            }
            if (parts.length < 2) {
                log.warn("Here, Invalid issuedBy format: {}, expected lastname.firstname.role.team", issuedBy);
                return null;
            }
            
            String lastname = parts[0];
            String firstname = parts[1];
            
            log.debug("Parsed lastname: '{}', firstname: '{}'", lastname, firstname);
            
            // Find the collaborateur by name
            Optional<Collaborateur> collaborateurOpt = collaborateurRepository.findByNomAndPrenom(lastname, firstname);
            
            if (collaborateurOpt.isPresent()) {
                Collaborateur collaborateur = collaborateurOpt.get();
                String cuid = collaborateur.getCUID();
                
                log.debug("Found collaborateur with CUID: {}", cuid);
                
                // Find the user by CUID (username)
                Optional<User> userOpt = userRepository.findByUsername(cuid);
                
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    log.info("Successfully found user with ID: {} for issuedBy: {}", user.getId(), issuedBy);
                    return user;
                } else {
                    log.warn("No user found with username (CUID): {}", cuid);
                }
            } else {
                log.warn("No collaborateur found with lastname: {} and firstname: {}", lastname, firstname);
            }
        } catch (Exception e) {
            log.error("Error finding user from issuedBy: {}", issuedBy, e);
        }
        
        return null;
    }
}