package org.xuannam.taskflowbackend.auth.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.xuannam.taskflowbackend.auth.dto.request.LoginRequest;
import org.xuannam.taskflowbackend.auth.dto.request.LogoutRequest;
import org.xuannam.taskflowbackend.auth.dto.request.RefreshTokenRequest;
import org.xuannam.taskflowbackend.auth.dto.request.RegisterRequest;
import org.xuannam.taskflowbackend.auth.dto.response.AccessTokenResponse;
import org.xuannam.taskflowbackend.auth.dto.response.LoginResponse;
import org.xuannam.taskflowbackend.auth.dto.response.RefreshTokenResponse;
import org.xuannam.taskflowbackend.auth.dto.response.RegisterResponse;
import org.xuannam.taskflowbackend.auth.security.AuthenticatedUser;
import org.xuannam.taskflowbackend.auth.security.JwtProperties;
import org.xuannam.taskflowbackend.auth.service.AuthService;
import org.xuannam.taskflowbackend.common.exception.BusinessException;
import org.xuannam.taskflowbackend.common.exception.ErrorCode;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/auth")
public class AuthController {
    AuthService authService;
    JwtProperties jwtProperties;
    private static final String REFRESH_TOKEN_COOKIE = "refresh_token";

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody @Valid RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse loginResponse = authService.login(request);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, refreshCookie(loginResponse.refreshToken(),
                        jwtProperties.refreshTokenExpiration()).toString())
                .body(new AccessTokenResponse(loginResponse.accessToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenResponse> refresh(@CookieValue(name = REFRESH_TOKEN_COOKIE, required = false) String refreshToken) {
        RefreshTokenResponse refreshTokenResponse = authService.refresh(new RefreshTokenRequest(requireRefreshToken(refreshToken)));
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, refreshCookie(refreshTokenResponse.refreshToken(),
                        jwtProperties.refreshTokenExpiration()).toString())
                .body(new AccessTokenResponse(refreshTokenResponse.accessToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                       @CookieValue(name = REFRESH_TOKEN_COOKIE, required = false) String refreshToken) {
        authService.logout(new LogoutRequest(requireRefreshToken(refreshToken)), authenticatedUser.userId());
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, refreshCookie("", Duration.ZERO).toString())
                .build();
    }

    @PostMapping("/logout-all")
    public ResponseEntity<Void> logoutAll(@AuthenticationPrincipal AuthenticatedUser principal) {
        authService.logoutAll(principal.userId());
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, refreshCookie("", Duration.ZERO).toString())
                .build();
    }

    private ResponseCookie refreshCookie(String value, Duration maxAge) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE, value)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/api/auth")
                .maxAge(maxAge)
                .build();
    }

    private String requireRefreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
        return refreshToken;
    }
}
