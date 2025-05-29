package com.sofrecom.tickit.controller;

import com.sofrecom.tickit.dto.*;
import com.sofrecom.tickit.model.ERole;
import com.sofrecom.tickit.model.NotificationType;
import com.sofrecom.tickit.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;

/**
 * REST controller for managing notifications.
 */
@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notifications", description = "API for managing user notifications")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    
    private final NotificationService notificationService;
    
    /**
     * GET /api/v1/notifications : Get all notifications with optional filtering.
     *
     * @param readFlag Optional filter for read status
     * @param type Optional filter for notification type
     * @param role Optional filter for target role
     * @param page The page number (0-based)
     * @param size The page size
     * @return the ResponseEntity with status 200 (OK) and the list of notifications
     */
    @GetMapping
    @Operation(
        summary = "Get notifications",
        description = "Retrieves notifications for the current user with optional filtering"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successfully retrieved notifications",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotificationResponse.class))
    )
    public ResponseEntity<List<NotificationResponse>> getNotifications(
            @Parameter(description = "Filter by read status") @RequestParam(required = false) Boolean readFlag,
            @Parameter(description = "Filter by notification type") @RequestParam(required = false) String type,
            @Parameter(description = "Filter by target role") @RequestParam(required = false) String role,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        
        log.debug("REST request to get notifications with filters: readFlag={}, type={}, role={}", readFlag, type, role);
        
        NotificationFilterRequest filter = NotificationFilterRequest.builder()
                .readFlag(readFlag)
                .build();
        
        // Parse type and role if provided
        if (type != null && !type.isEmpty()) {
            try {
                filter.setType(NotificationType.valueOf(type));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid notification type: {}", type);
            }
        }
        
        if (role != null && !role.isEmpty()) {
            try {
                filter.setRole(ERole.valueOf(role));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid role: {}", role);
            }
        }
        
        List<NotificationResponse> notifications = notificationService.getAll(filter, page, size);
        return ResponseEntity.ok(notifications);
    }
    
    /**
     * GET /api/v1/notifications/count : Get count of unread notifications.
     *
     * @return the ResponseEntity with status 200 (OK) and the count of unread notifications
     */
    @GetMapping("/count")
    @Operation(
        summary = "Count unread notifications",
        description = "Counts the number of unread notifications for the current user"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successfully counted unread notifications",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = CountResponse.class))
    )
    public ResponseEntity<CountResponse> countUnreadNotifications() {
        log.debug("REST request to count unread notifications");
        
        long count = notificationService.countUnread();
        return ResponseEntity.ok(new CountResponse(count));
    }
    
    /**
     * GET /api/v1/notifications/{id} : Get a specific notification.
     *
     * @param id The ID of the notification to retrieve
     * @return the ResponseEntity with status 200 (OK) and the notification, or with status 404 (Not Found)
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Get notification by ID",
        description = "Retrieves a specific notification by its ID"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successfully retrieved notification",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotificationResponse.class))
    )
    @ApiResponse(
        responseCode = "404",
        description = "Notification not found",
        content = @Content
    )
    public ResponseEntity<NotificationResponse> getNotification(@PathVariable Long id) {
        log.debug("REST request to get notification : {}", id);
        
        NotificationResponse notification = notificationService.getById(id);
        return ResponseEntity.ok(notification);
    }
    
    /**
     * POST /api/v1/notifications/read : Mark multiple notifications as read.
     *
     * @param request Request containing IDs of the notifications to mark as read
     * @return the ResponseEntity with status 200 (OK) and the number of notifications marked as read
     */
    @PostMapping("/read")
    @Operation(
        summary = "Mark notifications as read",
        description = "Marks multiple notifications as read"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successfully marked notifications as read",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = CountResponse.class))
    )
    public ResponseEntity<CountResponse> markAsRead(@Valid @RequestBody BulkIdsRequest request) {
        log.debug("REST request to mark notifications as read: {}", request.getIds());
        
        int count = notificationService.setReadBulk(request.getIds());
        return ResponseEntity.ok(new CountResponse(count));
    }
    
    /**
     * DELETE /api/v1/notifications : Delete multiple notifications.
     *
     * @param request Request containing IDs of the notifications to delete
     * @return the ResponseEntity with status 200 (OK) and the number of notifications deleted
     */
    @DeleteMapping
    @Operation(
        summary = "Delete notifications",
        description = "Deletes multiple notifications"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successfully deleted notifications",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = CountResponse.class))
    )
    public ResponseEntity<CountResponse> deleteNotifications(@Valid @RequestBody BulkIdsRequest request) {
        log.debug("REST request to delete notifications: {}", request.getIds());
        
        int count = notificationService.deleteBulk(request.getIds());
        return ResponseEntity.ok(new CountResponse(count));
    }
    
    /**
     * POST /api/v1/notifications/admin/create : Create notifications for users with specific roles.
     * Admin-only endpoint.
     *
     * @param request The notification creation request
     * @return the ResponseEntity with status 200 (OK) and the number of notifications created
     */
    @PostMapping("/admin/create")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Create notifications for roles",
        description = "Creates notifications for all users with specific roles (admin only)"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successfully created notifications",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = CountResponse.class))
    )
    public ResponseEntity<CountResponse> createNotifications(@Valid @RequestBody NotificationAdminCreateRequest request) {
        log.debug("REST request to create notifications for roles: {}", request.getRoles());
        
        int count = notificationService.createForRoles(
                request.getRoles(), 
                request.getType(), 
                request.getMessage(), 
                request.getPayload());
                
        return ResponseEntity.ok(new CountResponse(count));
    }
    
    /**
     * GET /api/v1/notifications/paged : Get paginated notifications with filtering capabilities.
     *
     * @param page Page number (1-based)
     * @param size Number of items per page
     * @param readFlag Filter by read status (true = read, false = unread)
     * @param type Filter by notification type
     * @param startDate Filter notifications after this date (format: yyyy-MM-dd)
     * @param endDate Filter notifications before this date (format: yyyy-MM-dd)
     * @param search Search in notification message
     * @return the ResponseEntity with status 200 (OK) and a paginated list of notifications
     */
    @GetMapping("/paged")
    @Operation(
        summary = "Get paginated notifications",
        description = "Retrieves a paginated list of notifications with filtering capabilities"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successfully retrieved paginated notifications",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = PagedNotificationResponse.class))
    )
    public ResponseEntity<PagedNotificationResponse> getPagedNotifications(
            @Parameter(description = "Page number (1-based)") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Filter by read status") @RequestParam(required = false) Boolean readFlag,
            @Parameter(description = "Filter by notification type") @RequestParam(required = false) String type,
            @Parameter(description = "Filter notifications after this date (format: yyyy-MM-dd)") 
                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Filter notifications before this date (format: yyyy-MM-dd)") 
                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Search in notification message") @RequestParam(required = false) String search) {
        
        log.debug("REST request to get paginated notifications: page={}, size={}, readFlag={}, type={}, startDate={}, endDate={}, search={}", 
                page, size, readFlag, type, startDate, endDate, search);
        
        // Convert 1-based page to 0-based
        int zeroBasedPage = Math.max(0, page - 1);
        
        // Convert LocalDate to LocalDateTime (start of day and end of day)
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.plusDays(1).atStartOfDay().minusNanos(1) : null;
        
        // Build notification filter request
        NotificationFilterRequest filter = NotificationFilterRequest.builder()
                .readFlag(readFlag)
                .createdAfter(startDateTime)
                .createdBefore(endDateTime)
                .searchText(search)
                .build();
        
        // Parse notification type if provided
        if (type != null && !type.isEmpty()) {
            try {
                filter.setType(NotificationType.valueOf(type));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid notification type: {}", type);
            }
        }
        
        Page<NotificationResponse> pagedResult = notificationService.getAllPaged(filter, zeroBasedPage, size);
        
        // Map to custom response format
        PagedNotificationResponse response = new PagedNotificationResponse(
                pagedResult.getContent(),
                pagedResult.getTotalElements(),
                pagedResult.getTotalPages(),
                pagedResult.getSize(),
                pagedResult.getNumber(),
                pagedResult.isFirst(),
                pagedResult.isLast()
        );
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * POST /api/v1/notifications/delete : Delete one or more notifications permanently.
     *
     * @param request Request containing IDs of the notifications to delete
     * @return the ResponseEntity with status 200 (OK) and the number of notifications deleted
     */
    @PostMapping("/delete")
    @Operation(
        summary = "Delete notifications",
        description = "Deletes one or more notifications permanently"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successfully deleted notifications",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = CountResponse.class))
    )
    public ResponseEntity<CountResponse> deleteNotificationsById(@Valid @RequestBody BulkIdsRequest request) {
        log.debug("REST request to delete notifications by IDs: {}", request.getIds());
        
        int count = notificationService.deleteNotifications(request.getIds());
        return ResponseEntity.ok(new CountResponse(count));
    }
}