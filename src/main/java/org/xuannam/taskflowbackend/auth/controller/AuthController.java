package org.xuannam.taskflowbackend.auth.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xuannam.taskflowbackend.auth.dto.request.LoginRequest;
import org.xuannam.taskflowbackend.auth.dto.request.RefreshTokenRequest;
import org.xuannam.taskflowbackend.auth.dto.request.RegisterRequest;
import org.xuannam.taskflowbackend.auth.dto.response.LoginResponse;
import org.xuannam.taskflowbackend.auth.dto.response.RefreshTokenResponse;
import org.xuannam.taskflowbackend.auth.dto.response.RegisterResponse;
import org.xuannam.taskflowbackend.auth.service.AuthService;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/auth")
public class AuthController {
    AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody @Valid RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refresh(@RequestBody @Valid RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }
}
