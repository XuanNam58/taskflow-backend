package org.xuannam.taskflowbackend.common.response;

import lombok.Builder;

import java.time.Instant;

@Builder
public record ErrorResponse(
        String code,
        String message,
        Instant timestamp,
        String path
) {
}
