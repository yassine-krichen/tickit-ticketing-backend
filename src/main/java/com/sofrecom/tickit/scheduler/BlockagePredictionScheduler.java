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

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class BlockagePredictionScheduler {

    private final ActeTraitementService acteTraitementService;
    private final AIService aiService;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    private static final int MAX_MESSAGE_LENGTH = 500;

    @Scheduled(cron = "0 0 2 */3 * *")
    public void predictBlockages() {
        log.info("Starting daily blockage prediction check");

        try {
            List<ActeTraitementResponse> allActs = acteTraitementService.getAllActeTraitements();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime sevenDaysFromNow = now.plusDays(7);

            List<ActeTraitementResponse> upcomingActs = allActs.stream()
                .filter(act -> {
                    LocalDateTime dueDate = getDueDate(act);
                    return dueDate != null && dueDate.isAfter(now) && dueDate.isBefore(sevenDaysFromNow);
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

            String riskLevel = prediction.getRiskLevel().toString().toLowerCase();
            String truncatedExplanation = truncateExplanation(prediction.getExplanation(), 200);

            String message = String.format("%s risk: Act %s (Case: %s) - %s",
                capitalize(riskLevel), act.getIdacte(), act.getCaseId(), truncatedExplanation);

            if (message.length() > MAX_MESSAGE_LENGTH) {
                message = message.substring(0, MAX_MESSAGE_LENGTH - 3) + "...";
            }

            notificationService.createForRoles(
                Collections.singleton(ERole.ROLE_PILOTE),
                prediction.getRiskLevel() == BlockagePredictionResponse.RiskLevel.HIGH
                    ? NotificationType.CASE_BLOCKED : NotificationType.DUE_DATE_APPROACHING,
                message,
                payload
            );
        } catch (Exception e) {
            log.error("Error creating notification for act {}: {}", act.getIdacte(), e.getMessage());
        }
    }

    private String truncateExplanation(String explanation, int maxLength) {
        if (explanation == null || explanation.length() <= maxLength) return explanation;
        int lastPeriod = explanation.substring(0, maxLength).lastIndexOf('.');
        if (lastPeriod > 0) return explanation.substring(0, lastPeriod + 1);
        return explanation.substring(0, maxLength - 3) + "...";
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private LocalDateTime getDueDate(ActeTraitementResponse act) {
        try {
            if (act.getDateDeadline() != null) {
                return new Timestamp(act.getDateDeadline().getTime()).toLocalDateTime();
            } else if (act.getDateReception() != null && act.getDuree() > 0) {
                return new Timestamp(act.getDateReception().getTime()).toLocalDateTime().plusDays(act.getDuree());
            }
        } catch (Exception e) {
            log.error("Error converting date for act {}: {}", act.getIdacte(), e.getMessage());
        }
        return null;
    }
}
