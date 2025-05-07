package com.sofrecom.tickit.scheduler;

import com.sofrecom.tickit.config.AutomationProperties;
import com.sofrecom.tickit.service.AutomationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class AutomationScheduler {
    private final AutomationService automationService;
    private final AutomationProperties properties;
    
    /**
     * Scheduled task to process overdue cases.
     * Changes case status to BLOCKED and creates tickets for overdue treatment acts.
     */
    @Scheduled(cron = "${tickit.automation.overdueCasesSchedule}")
    public void checkOverdueCases() {
        log.info("Running scheduled task: Overdue case detection");
        automationService.processOverdueCases();
    }
    
    /**
     * Scheduled task to handle resolution SLA breaches.
     * Escalates the priority of in-progress tickets where resolution SLA is breached.
     */
    @Scheduled(cron = "${tickit.automation.resolutionSlaSchedule}")
    public void checkResolutionSlaBreaches() {
        log.info("Running scheduled task: Resolution SLA breach detection");
        automationService.processResolutionSlaBreaches();
    }
    
    /**
     * Scheduled task to handle response SLA breaches.
     * Escalates the priority of open tickets where response SLA is breached.
     */
    @Scheduled(cron = "${tickit.automation.responseSlaSchedule}")
    public void checkResponseSlaBreaches() {
        log.info("Running scheduled task: Response SLA breach detection");
        automationService.processResponseSlaBreaches();
    }
}
