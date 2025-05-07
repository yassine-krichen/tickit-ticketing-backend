package com.sofrecom.tickit.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.sofrecom.tickit.dto.NotificationFilterRequest;
import com.sofrecom.tickit.dto.NotificationResponse;
import com.sofrecom.tickit.exception.ResourceNotFoundException;
import com.sofrecom.tickit.exception.UnauthorizedOperationException;
import com.sofrecom.tickit.model.ERole;
import com.sofrecom.tickit.model.Notification;
import com.sofrecom.tickit.model.NotificationType;
import com.sofrecom.tickit.model.User;
import com.sofrecom.tickit.repository.NotificationRepository;
import com.sofrecom.tickit.repository.UserRepository;
import com.sofrecom.tickit.service.NotificationService;
import com.sofrecom.tickit.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the NotificationService interface.
 * Provides logic for creating, retrieving, updating, and deleting notifications.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final AuthUtils authUtils;
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public NotificationResponse createForUser(Long userId, NotificationType type, String message, JsonNode payload) {
        log.debug("Creating notification of type {} for user {}", type, userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        Notification notification = Notification.builder()
                .userRef(user)
                .type(type)
                .message(message)
                .payload(payload)
                .readFlag(false)
                .createdAt(LocalDateTime.now())
                .build();
        
        Notification savedNotification = notificationRepository.save(notification);
        return mapToResponse(savedNotification);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public int createForRoles(Set<ERole> roles, NotificationType type, String message, JsonNode payload) {
        log.debug("Creating notifications of type {} for roles: {}", type, roles);
        
        // Convert ERole enum values to their string representation
        Set<String> roleNames = roles.stream()
                .map(Enum::name)
                .collect(Collectors.toSet());
        
        // Find all users with the specified roles
        List<User> users = userRepository.findDistinctByRoles_NameIn(roleNames);
        
        if (users.isEmpty()) {
            log.warn("No users found with roles: {}", roles);
            return 0;
        }
        
        List<Notification> notifications = new ArrayList<>();
        
        for (User user : users) {
            Notification notification = Notification.builder()
                    .userRef(user)
                    .target(roles)
                    .type(type)
                    .message(message)
                    .payload(payload)
                    .readFlag(false)
                    .createdAt(LocalDateTime.now())
                    .build();
            
            notifications.add(notification);
        }
        
        notificationRepository.saveAll(notifications);
        return notifications.size();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getAll(NotificationFilterRequest filter, int page, int size) {
        log.debug("Getting notifications with filter: {}, page: {}, size: {}", filter, page, size);
        
        User currentUser = getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Specification<Notification> specification = createSpecification(filter, currentUser);
        Page<Notification> notifications = notificationRepository.findAll(specification, pageable);
        
        return notifications.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public NotificationResponse getById(Long id) {
        log.debug("Getting notification by id: {}", id);
        
        User currentUser = getCurrentUser();
        
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", id));
        
        // Check if the notification belongs to the current user
        if (!notification.getUserRef().getId().equals(currentUser.getId())) {
            throw new UnauthorizedOperationException("You don't have permission to view this notification");
        }
        
        return mapToResponse(notification);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public int setReadBulk(List<Long> ids) {
        log.debug("Marking notifications as read: {}", ids);
        
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        
        User currentUser = getCurrentUser();
        
        // Verify all notifications belong to the current user
        List<Notification> notifications = notificationRepository.findByIdInAndUserRef(ids, currentUser);
        
        if (notifications.size() != ids.size()) {
            Set<Long> foundIds = notifications.stream()
                    .map(Notification::getId)
                    .collect(Collectors.toSet());
            
            List<Long> unauthorizedIds = ids.stream()
                    .filter(id -> !foundIds.contains(id))
                    .collect(Collectors.toList());
            
            throw new UnauthorizedOperationException("Cannot mark notifications as read: " + unauthorizedIds);
        }
        
        return notificationRepository.markAsRead(ids, LocalDateTime.now());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public int deleteBulk(List<Long> ids) {
        log.debug("Deleting notifications: {}", ids);
        
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        
        User currentUser = getCurrentUser();
        return notificationRepository.deleteByIdInAndUserRef(ids, currentUser);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public long countUnread() {
        User currentUser = getCurrentUser();
        return notificationRepository.countByUserRefAndReadFlagFalse(currentUser);
    }
    
    /**
     * Creates a specification for filtering notifications.
     * 
     * @param filter The filter criteria
     * @param user The current user
     * @return A specification for filtering notifications
     */
    private Specification<Notification> createSpecification(NotificationFilterRequest filter, User user) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Always filter by current user
            predicates.add(cb.equal(root.get("userRef"), user));
            
            // Apply filter conditions if provided
            if (filter != null) {
                if (filter.getReadFlag() != null) {
                    predicates.add(cb.equal(root.get("readFlag"), filter.getReadFlag()));
                }
                
                if (filter.getType() != null) {
                    predicates.add(cb.equal(root.get("type"), filter.getType()));
                }
                
                if (filter.getRole() != null) {
                    Join<Notification, Set<ERole>> targetJoin = root.join("target");
                    predicates.add(cb.equal(targetJoin, filter.getRole()));
                }
                
                if (filter.getCreatedAfter() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), filter.getCreatedAfter()));
                }
                
                if (filter.getCreatedBefore() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), filter.getCreatedBefore()));
                }
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
    
    /**
     * Gets the current authenticated user.
     * 
     * @return The current user
     * @throws IllegalStateException if the current user cannot be found
     */
    private User getCurrentUser() {
        String username = authUtils.getAuthenticatedUsername();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Current user could not be found"));
    }
    
    /**
     * Maps a Notification entity to a NotificationResponse.
     * 
     * @param notification The notification entity
     * @return The notification response
     */
    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .userId(notification.getUserRef().getId())
                .target(notification.getTarget())
                .message(notification.getMessage())
                .payload(notification.getPayload())
                .readFlag(notification.getReadFlag())
                .createdAt(notification.getCreatedAt())
                .readAt(notification.getReadAt())
                .type(notification.getType())
                .build();
    }
}