package org.xuannam.taskflowbackend.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // ===== Common =====
    INTERNAL_SERVER_ERROR("COMMON_001", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    VALIDATION_ERROR("COMMON_002", "Validation failed", HttpStatus.BAD_REQUEST),
    METHOD_NOT_ALLOWED("COMMON_003", "Method not allowed", HttpStatus.METHOD_NOT_ALLOWED),
    RESOURCE_NOT_FOUND("COMMON_004", "Resource not found", HttpStatus.NOT_FOUND),

    // ===== Authentication =====
    INVALID_CREDENTIALS("AUTH_001", "Invalid email or password", HttpStatus.UNAUTHORIZED),
    INVALID_ACCESS_TOKEN("AUTH_002", "Invalid access token", HttpStatus.UNAUTHORIZED),
    ACCESS_TOKEN_EXPIRED("AUTH_003", "Access token has expired", HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN("AUTH_004", "Invalid refresh token", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_EXPIRED("AUTH_005", "Refresh token has expired", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_REVOKED("AUTH_006", "Refresh token has been revoked", HttpStatus.UNAUTHORIZED),
    USER_DISABLED("AUTH_007", "User has been disabled", HttpStatus.FORBIDDEN),

    // ===== Authorization =====
    ACCESS_DENIED("SECURITY_001", "Access denied", HttpStatus.FORBIDDEN),

    // ===== User =====
    EMAIL_ALREADY_EXISTS("USER_001", "Email already exists", HttpStatus.CONFLICT),
    USERNAME_ALREADY_EXISTS("USER_002", "Username already exists", HttpStatus.CONFLICT);
    
    private final String code;
    private final String message;
    private final HttpStatus status;
}
