package com.sofrecom.tickit.dto;

import com.sofrecom.tickit.model.Case;
import com.sofrecom.tickit.model.CaseType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * Data Transfer Object for updating an existing Case.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseUpdate {

    @NotBlank(message = "Case name must not be blank")
    private String name;

    @NotBlank(message = "Case description must not be blank")
    private String description;

    @NotEmpty(message = "Case tags must not be empty")
    private List<String> tags;

    // Using enum from the Case class for type.
    private CaseType type;

    private Case.Status status;
    private Case.Priority priority;
    private Case.Team team;
}
