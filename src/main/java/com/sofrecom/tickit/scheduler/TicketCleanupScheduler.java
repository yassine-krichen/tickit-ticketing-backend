package com.sofrecom.tickit.scheduler;

import com.sofrecom.tickit.service.TicketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

/**
 * Scheduled tasks related to ticket management.
 */
@Component
@RequiredArgsConstructor
public class TicketCleanupScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TicketCleanupScheduler.class);
    private final TicketService ticketService;

    /**
     * Periodically deletes old, closed tickets.
     * This job runs every day at 2:00 AM server time.
     * The cron expression is "second minute hour day-of-month month day-of-week".
     * "0 0 2 * * ?" means:
     * - 0 seconds
     * - 0 minutes
     * - 2nd hour (2 AM)
     * - * (any day of the month)
     * - * (any month)
     * - ? (any day of the week)
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void performOldClosedTicketsCleanup() {
        LOGGER.info("Scheduled task: [PerformOldClosedTicketsCleanup] - Initiating...");
        try {
            int deletedCount = ticketService.autoDeleteOldClosedTickets();
            LOGGER.info("Scheduled task: [PerformOldClosedTicketsCleanup] - Completed. {} tickets deleted.", deletedCount);
        } catch (Exception e) {
            LOGGER.error("Scheduled task: [PerformOldClosedTicketsCleanup] - Encountered an error.", e);
        }
    }
}