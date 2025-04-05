package com.sofrecom.tickit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VerificationResponse {
    private boolean valid;
}
