package org.xuannam.taskflowbackend.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.xuannam.taskflowbackend.common.exception.ErrorCode;
import org.xuannam.taskflowbackend.common.response.ErrorResponse;

import java.io.IOException;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
    private final ObjectMapper objectMapper;
    
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ErrorCode errorCode = ErrorCode.INVALID_ACCESS_TOKEN;
        
        if (authException.getCause() instanceof ExpiredJwtException) {
            errorCode = ErrorCode.ACCESS_TOKEN_EXPIRED;
        }
        
        response.setStatus(errorCode.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ErrorResponse body = ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .build();
        
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
