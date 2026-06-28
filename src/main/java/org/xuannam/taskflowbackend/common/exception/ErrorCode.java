package org.xuannam.taskflowbackend.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    EMAIL_ALREADY_EXISTS("AUTH_001", "Email already exists", HttpStatus.CONFLICT),
    USERNAME_ALREADY_EXISTS("AUTH_002", "Username already exists", HttpStatus.CONFLICT),
    EMAIL_NOT_EXIST("AUTH_003", "Email not exist", HttpStatus.BAD_REQUEST),
    WRONG_PASSWORD("AUTH_004", "Wrong password", HttpStatus.BAD_REQUEST),
    VALIDATION_ERROR("COMMON_001", "Validation failed", HttpStatus.BAD_REQUEST),
    METHOD_NOT_ALLOWED("COMMON_002", "Method not allowed", HttpStatus.METHOD_NOT_ALLOWED),
    INTERNAL_ERROR("COMMON_999", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
    
    private final String code;
    private final String message;
    private final HttpStatus status;
}
