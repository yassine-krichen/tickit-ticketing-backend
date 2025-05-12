package com.sofrecom.tickit.listener;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

/**
 * Event triggered when a case is blocked.
 */
@Getter
@RequiredArgsConstructor
public class CaseBlockedEvent {
    private final UUID caseId;
    private final String caseName;
    private final UUID ticketId; // Can be null if not triggered by a ticket
}