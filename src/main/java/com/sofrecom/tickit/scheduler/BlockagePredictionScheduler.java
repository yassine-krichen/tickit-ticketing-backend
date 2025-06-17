package com.sofrecom.tickit.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sofrecom.tickit.dto.ActeTraitementResponse;
import com.sofrecom.tickit.dto.BlockagePredictionResponse;
import com.sofrecom.tickit.model.ERole;
import com.sofrecom.tickit.model.NotificationType;
import com.sofrecom.tickit.service.AIService;
import com.sofrecom.tickit.service.ActeTraitementService;
import com.sofrecom.tickit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.sql.Timestamp;

@Component
@RequiredArgsConstructor
@Slf4j
public class BlockagePredictionScheduler {

    private final ActeTraitementService acteTraitementService;
    private final AIService aiService;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    private static final int MAX_MESSAGE_LENGTH = 500;
    
    @Scheduled(cron = "0 0 2 */3 * *") // Runs at 2 AM every 3 days
    // @Scheduled(cron = "0 49 0 * * *") // for testing
    public void predictBlockages() {
        log.info("Starting daily blockage prediction check");
        
        try {
            // Get all active treatment acts
            List<ActeTraitementResponse> allActs = acteTraitementService.getAllActeTraitements();
            
            // Filter acts that are within 7 days of their due date
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime sevenDaysFromNow = now.plusDays(7);
            
            List<ActeTraitementResponse> upcomingActs = allActs.stream()
                .filter(act -> {
                    LocalDateTime dueDate = getDueDate(act);
                    return dueDate != null && 
                           dueDate.isAfter(now) && 
                           dueDate.isBefore(sevenDaysFromNow);
                })
                .collect(Collectors.toList());

            log.info("Found {} treatment acts approaching due date", upcomingActs.size());
            
            int predictionsCount = 0;
            
            for (ActeTraitementResponse act : upcomingActs) {
                try {
                    BlockagePredictionResponse prediction = aiService.predictBlockageRiskWithDetails(act.getIdacte());
                    
                    if (prediction.getRiskLevel() != BlockagePredictionResponse.RiskLevel.LOW) {
                        createBlockageRiskNotification(act, prediction);
                        predictionsCount++;
                        log.debug("Created blockage risk notification for act: {} with risk level: {}", 
                                act.getIdacte(), prediction.getRiskLevel());
                    }
                } catch (Exception e) {
                    log.error("Error predicting blockage for act {}: {}", act.getIdacte(), e.getMessage());
                }
            }

            log.info("Completed blockage prediction check. Generated {} predictions out of {} candidates", 
                    predictionsCount, upcomingActs.size());
                    
        } catch (Exception e) {
            log.error("Error during blockage prediction scheduled job: {}", e.getMessage(), e);
        }
    }

    private void createBlockageRiskNotification(ActeTraitementResponse act, BlockagePredictionResponse prediction) {
        try {
            ObjectNode payload = objectMapper.createObjectNode()
                .put("actId", act.getIdacte())
                .put("caseId", act.getCaseId().toString())
                .put("prestationType", act.getPrestationName())
                .put("riskLevel", prediction.getRiskLevel().toString())
                .put("riskScore", prediction.getRiskScore());

            // Create a concise message
            String riskLevel = prediction.getRiskLevel().toString().toLowerCase();
            String truncatedExplanation = truncateExplanation(prediction.getExplanation(), 200); // Leave room for the rest
            
            String message = String.format(
                "%s risk: Act %s (Case: %s) - %s", 
                capitalize(riskLevel),
                act.getIdacte(),
                act.getCaseId(),
                truncatedExplanation
            );

            // Ensure message doesn't exceed max length
            if (message.length() > MAX_MESSAGE_LENGTH) {
                message = message.substring(0, MAX_MESSAGE_LENGTH - 3) + "...";
            }

            notificationService.createForRoles(
                Collections.singleton(ERole.ROLE_PILOTE),
                prediction.getRiskLevel() == BlockagePredictionResponse.RiskLevel.HIGH ? 
                    NotificationType.CASE_BLOCKED : NotificationType.DUE_DATE_APPROACHING,
                message,
                payload
            );
            
            log.debug("Created notification for act {} with message length: {}", 
                    act.getIdacte(), message.length());
            
        } catch (Exception e) {
            log.error("Error creating notification for act {}: {}", act.getIdacte(), e.getMessage());
        }
    }

    private String truncateExplanation(String explanation, int maxLength) {
        if (explanation == null || explanation.length() <= maxLength) {
            return explanation;
        }
        // Find the last complete sentence within the limit
        int lastPeriod = explanation.substring(0, maxLength).lastIndexOf('.');
        if (lastPeriod > 0) {
            return explanation.substring(0, lastPeriod + 1);
        }
        // If no sentence end found, cut at maxLength
        return explanation.substring(0, maxLength - 3) + "...";
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private LocalDateTime getDueDate(ActeTraitementResponse act) {
        try {
            if (act.getDateDeadline() != null) {
                return new Timestamp(act.getDateDeadline().getTime())
                    .toLocalDateTime();
            } else if (act.getDateReception() != null && act.getDuree() > 0) {
                return new Timestamp(act.getDateReception().getTime())
                    .toLocalDateTime()
                    .plusDays(act.getDuree());
            }
        } catch (Exception e) {
            log.error("Error converting date for act {}: {}", act.getIdacte(), e.getMessage());
        }
        return null;
    }
}
