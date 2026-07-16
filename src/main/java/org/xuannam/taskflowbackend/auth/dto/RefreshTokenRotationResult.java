package org.xuannam.taskflowbackend.auth.dto;

public record RefreshTokenRotationResult(
        Long userId,
        String newRefreshToken
) {
}
