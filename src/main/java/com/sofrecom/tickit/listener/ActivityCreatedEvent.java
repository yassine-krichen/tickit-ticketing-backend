package com.sofrecom.tickit.listener;

import com.sofrecom.tickit.model.TicketActivity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Event that gets fired when a ticket activity is created.
 * Used to decouple ticket activity creation from ticket status updates.
 */
@Getter
public class ActivityCreatedEvent extends ApplicationEvent {
    private final TicketActivity activity;

    public ActivityCreatedEvent(TicketActivity activity) {
        super(activity);
        this.activity = activity;
    }
}