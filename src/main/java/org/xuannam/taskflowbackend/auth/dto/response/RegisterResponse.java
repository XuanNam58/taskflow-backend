package org.xuannam.taskflowbackend.auth.dto.response;

import lombok.Builder;

@Builder
public record RegisterResponse(
        Long id,
        String email,
        String username
) {
}
