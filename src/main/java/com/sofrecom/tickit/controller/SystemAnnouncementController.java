package com.sofrecom.tickit.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sofrecom.tickit.dto.AnnouncementRequest;
import com.sofrecom.tickit.model.NotificationType;
import com.sofrecom.tickit.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/announcements")
@Tag(name = "System Announcements", description = "API for managing system-wide announcements")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@Slf4j
public class SystemAnnouncementController {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Create system announcement",
        description = "Creates a system-wide announcement that will be sent as notifications to users with specified roles"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Announcement successfully sent",
        content = @Content(mediaType = "application/json")
    )
    public ResponseEntity<Map<String, Object>> createAnnouncement(@Valid @RequestBody AnnouncementRequest request) {
        log.info("Creating system announcement: {}", request.getTitle());
        
        // Create payload with announcement details
        JsonNode payload = objectMapper.createObjectNode()
                .put("title", request.getTitle())
                .put("severity", request.getSeverity().name());
        
        // Create message based on title and content
        String message = request.getTitle();
        if (request.getContent() != null && !request.getContent().isEmpty()) {
            message += " - " + request.getContent();
        }
        
        // Create notifications for target roles
        int count = notificationService.createForRoles(
                request.getTargetRoles(),
                NotificationType.SYSTEM_ANNOUNCEMENT,
                message,
                payload
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Announcement sent successfully");
        response.put("count", count);
        return ResponseEntity.ok(response);
    }
}