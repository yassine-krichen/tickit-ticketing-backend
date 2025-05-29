// UserResponseDTO.java
package com.sofrecom.tickit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    /** Response Message */
    private String message;

    /** User Email */
    private String email;

    /** Fingerprint in format lastName.firstName.role.team */
    private String fingerprint;

    /** URL or Base64 payload of avatar */
    private String avatarUrl;
}
