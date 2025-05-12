package com.sofrecom.tickit.listener;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

/**
 * Event triggered when a due date is approaching.
 */
@Getter
@RequiredArgsConstructor
public class DueDateApproachingEvent {
    private final UUID caseId;
    private final String caseName;
    private final String acteTraitementId;
    private final int daysRemaining;
}