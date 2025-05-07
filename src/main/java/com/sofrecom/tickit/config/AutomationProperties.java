package com.sofrecom.tickit.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tickit.automation")
@Data
public class AutomationProperties {
    // Cron schedules
    private String overdueCasesSchedule = "0 0 * * * *";  // Default: Every hour
    private String resolutionSlaSchedule = "0 */15 * * * *";  // Default: Every 15 minutes
    private String responseSlaSchedule = "0 */5 * * * *";  // Default: Every 5 minutes
    
    // Business rules
    private int minimumDurationDays = 1;  // Minimum duration for treatment acts in days
    private boolean createTicketsForOverdueCases = true;  // Toggle automatic ticket creation
}
