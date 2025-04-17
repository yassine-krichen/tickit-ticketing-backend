package com.sofrecom.tickit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketCount {
    private Object category; // Can be Status, Type, Step, SubStatus, etc.
    private Long count;
    
    // Additional constructor for specifying the category type explicitly
    public TicketCount(String categoryType, Object category, Long count) {
        this.category = categoryType + ":" + category;
        this.count = count;
    }
}
