package com.sofrecom.tickit.listener;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

/**
 * Event triggered when an activity has been stalled for too long.
 */
@Getter
@RequiredArgsConstructor
public class ActivityStalledEvent {
    private final UUID ticketId;
    private final Long activityId;
    private final String activityType;
    private final String username;
    private final int daysSinceUpdate;
}