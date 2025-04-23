package com.sofrecom.tickit.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.sofrecom.tickit.repository.TicketActivityRepository;

import java.time.LocalDateTime;

@Component
public class SoftDeleteCleanupScheduler {

    private final TicketActivityRepository repo;

    public SoftDeleteCleanupScheduler(TicketActivityRepository repo) {
        this.repo = repo;
    }

    /**
     * Runs once a day at 2:00 AM, deletes activities soft-deleted over 30 days ago.
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void purgeOldSoftDeletes() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(30);
        repo.deleteAllSoftDeletedBefore(threshold);
    }
}