package com.sofrecom.tickit.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sofrecom.tickit.model.NotificationType;
import com.sofrecom.tickit.model.Ticket;
import com.sofrecom.tickit.model.User;
import com.sofrecom.tickit.repository.UserRepository;
import com.sofrecom.tickit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import com.sofrecom.tickit.model.ERole;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class TicketNotificationListener {

    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Async
    @TransactionalEventListener
    public void handleTicketCreatedEvent(TicketCreatedEvent event) {
        Ticket ticket = event.getTicket();
        String message = String.format("New ticket created: %s", ticket.getSubject());
        
        // Create payload with ticket details
        JsonNode payload = createTicketPayload(ticket);
        
        // Notify all admins and supervisors
        Set<ERole> roles = createRoleSet(ERole.ROLE_ADMIN, ERole.ROLE_DIRECTION);
        notificationService.createForRoles(
                roles,
                NotificationType.TICKET_CREATED,
                message,
                payload
        );
        
        // If the ticket was created by a user (not system), also notify that user
        if (ticket.getIssuedBy() != null && !ticket.getIssuedBy().startsWith("System-")) {
            User user = userRepository.findByUsername(ticket.getIssuedBy()).orElse(null);
            if (user != null) {
                notificationService.createForUser(
                        user.getId(),
                        NotificationType.TICKET_CREATED,
                        message,
                        payload
                );
            }
        }
    }

    @Async
    @TransactionalEventListener
    public void handleTicketResolvedEvent(TicketResolvedEvent event) {
        Ticket ticket = event.getTicket();
        String message = String.format("Ticket resolved: %s", ticket.getSubject());
        
        // Create payload with ticket details
        JsonNode payload = createTicketPayload(ticket);
        
        // Notify the creator of the ticket
        if (ticket.getIssuedBy() != null) {
            User user = userRepository.findByUsername(ticket.getIssuedBy()).orElse(null);
            if (user != null) {
                notificationService.createForUser(
                        user.getId(),
                        NotificationType.TICKET_RESOLVED,
                        message,
                        payload
                );
            }
        }
        
        // Also notify admins
        Set<ERole> roles = createRoleSet(ERole.ROLE_ADMIN);
        notificationService.createForRoles(
                roles,
                NotificationType.TICKET_RESOLVED,
                message,
                payload
        );
    }

    @Async
    @TransactionalEventListener
    public void handleTicketUpdatedEvent(TicketUpdatedEvent event) {
        Ticket ticket = event.getTicket();
        String message = String.format("Ticket updated: %s", ticket.getSubject());
        
        // Create payload with ticket details
        JsonNode payload = createTicketPayload(ticket);
        
        // Notify ticket creator
        if (ticket.getIssuedBy() != null) {
            User user = userRepository.findByUsername(ticket.getIssuedBy()).orElse(null);
            if (user != null) {
                notificationService.createForUser(
                        user.getId(),
                        NotificationType.UPDATE,
                        message,
                        payload
                );
            }
        }
        
        // Notify all users who have worked on this ticket (from activities)
        List<User> activityUsers = findUsersWithActivitiesOnTicket(ticket.getId());
        for (User user : activityUsers) {
            notificationService.createForUser(
                    user.getId(),
                    NotificationType.UPDATE,
                    message,
                    payload
            );
        }
    }
    
    @Async
    @TransactionalEventListener
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
        notificationService.createForRoles(
                roles,
                NotificationType.PRIO_LEVELED,
                message,
                payload
        );
        
        // Notify the creator of the ticket
        if (ticket.getIssuedBy() != null) {
            User user = userRepository.findByUsername(ticket.getIssuedBy()).orElse(null);
            if (user != null) {
                notificationService.createForUser(
                        user.getId(),
                        NotificationType.PRIO_LEVELED,
                        message,
                        payload
                );
            }
        }
    }
    
    @Async
    @TransactionalEventListener
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
        notificationService.createForRoles(
                roles,
                NotificationType.SLA_BREACH,
                message,
                payload
        );
    }
    
    @Async
    @TransactionalEventListener
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
        
        // Notify the user who owns the activity
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            notificationService.createForUser(
                    user.getId(),
                    NotificationType.ACTIVITY_STALLED,
                    message,
                    payload
            );
        }
        
        // Also notify direction
        Set<ERole> roles = createRoleSet(ERole.ROLE_DIRECTION);
        notificationService.createForRoles(
                roles,
                NotificationType.ACTIVITY_STALLED,
                message,
                payload
        );
    }
    
    @Async
    @TransactionalEventListener
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
        notificationService.createForRoles(
                roles,
                NotificationType.CASE_BLOCKED,
                message,
                payload
        );
    }
    
    @Async
    @TransactionalEventListener
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
        notificationService.createForRoles(
                roles,
                NotificationType.DUE_DATE_APPROACHING,
                message,
                payload
        );
    }

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
    
    private List<User> findUsersWithActivitiesOnTicket(UUID ticketId) {
        // This is a placeholder - you would need to implement a repository method to find users
        // who have worked on activities for a given ticket
        return userRepository.findUsersByTicketActivities(ticketId);
    }

    private Set<ERole> createRoleSet(ERole... roles) {
        Set<ERole> roleSet = new HashSet<>();
        for (ERole role : roles) {
            roleSet.add(role);
        }
        return roleSet;
    }
}