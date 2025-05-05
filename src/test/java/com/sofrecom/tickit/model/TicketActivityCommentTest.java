package com.sofrecom.tickit.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class TicketActivityCommentTest {

    @Test
    public void testPrePersist() {
        // Given
        TicketActivityComment comment = new TicketActivityComment();
        assertNull(comment.getCreatedAt());
        assertNull(comment.getUpdatedAt());
        
        // When
        comment.onCreate();
        
        // Then
        assertNotNull(comment.getCreatedAt());
        assertNull(comment.getUpdatedAt(), "updatedAt should still be null after creation");
    }
    
    @Test
    public void testPreUpdate() {
        // Given
        TicketActivityComment comment = new TicketActivityComment();
        LocalDateTime creationTime = LocalDateTime.now().minusDays(1);
        comment.setCreatedAt(creationTime);
        assertNull(comment.getUpdatedAt());
        
        // When
        comment.onUpdate();
        
        // Then
        assertEquals(creationTime, comment.getCreatedAt(), "createdAt should not change on update");
        assertNotNull(comment.getUpdatedAt(), "updatedAt should be set after update");
    }
}