package com.sofrecom.tickit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BlockchainVerificationResponse {
    private boolean valid;
    private String message;
}
