package com.sofrecom.tickit.dto;

import com.sofrecom.tickit.model.ERole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for creating a system-wide announcement")
public class AnnouncementRequest {
    
    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    @Schema(description = "Title of the announcement", required = true, example = "System Maintenance")
    private String title;
    
    @Size(max = 1000, message = "Content cannot exceed 1000 characters")
    @Schema(description = "Detailed content of the announcement", example = "The system will be down for maintenance on Saturday from 2-4 AM.")
    private String content;
    
    @NotNull(message = "Severity is required")
    @Schema(description = "Severity level of the announcement", required = true)
    private AnnouncementSeverity severity;
    
    @NotEmpty(message = "At least one target role is required")
    @Schema(description = "Roles that should receive this announcement", required = true)
    private Set<ERole> targetRoles;
    
    public enum AnnouncementSeverity {
        INFO,
        WARNING,
        CRITICAL
    }
}