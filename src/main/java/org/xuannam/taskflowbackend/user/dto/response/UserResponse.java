package org.xuannam.taskflowbackend.user.dto.response;

import java.time.Instant;

public record UserResponse(
        Long id,
        String email,
        String username,
        boolean enabled,
        Instant createdAt,
        Instant modifiedAt
) {
}
