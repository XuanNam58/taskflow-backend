package org.xuannam.taskflowbackend.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank
        String email,
        
        @NotBlank
        String password
) {
}
