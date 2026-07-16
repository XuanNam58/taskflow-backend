package org.xuannam.taskflowbackend.auth.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xuannam.taskflowbackend.auth.dto.RefreshTokenRotationResult;
import org.xuannam.taskflowbackend.auth.dto.request.LoginRequest;
import org.xuannam.taskflowbackend.auth.dto.request.RefreshTokenRequest;
import org.xuannam.taskflowbackend.auth.dto.request.RegisterRequest;
import org.xuannam.taskflowbackend.auth.dto.response.LoginResponse;
import org.xuannam.taskflowbackend.auth.dto.response.RefreshTokenResponse;
import org.xuannam.taskflowbackend.auth.dto.response.RegisterResponse;
import org.xuannam.taskflowbackend.auth.entity.RefreshTokenEntity;
import org.xuannam.taskflowbackend.auth.repository.RefreshTokenRepository;
import org.xuannam.taskflowbackend.auth.security.JwtProperties;
import org.xuannam.taskflowbackend.auth.security.JwtService;
import org.xuannam.taskflowbackend.auth.service.AuthService;
import org.xuannam.taskflowbackend.auth.service.RefreshTokenService;
import org.xuannam.taskflowbackend.common.exception.BusinessException;
import org.xuannam.taskflowbackend.common.exception.ErrorCode;
import org.xuannam.taskflowbackend.user.entity.UserEntity;
import org.xuannam.taskflowbackend.user.mapper.UserMapper;
import org.xuannam.taskflowbackend.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {
    PasswordEncoder passwordEncoder;
    UserRepository userRepository;
    UserMapper userMapper;
    JwtService jwtService;
    JwtProperties jwtProperties;
    RefreshTokenService refreshTokenService;
    RefreshTokenRepository refreshTokenRepository;

    @Transactional
    @Override
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }

        UserEntity user = new UserEntity();
        user.setEmail(request.email());
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setEnabled(true);
        
        UserEntity savedUser = userRepository.save(user);
        return userMapper.toRegisterResponse(savedUser);
    }

    @Transactional
    @Override
    public LoginResponse login(LoginRequest request) {
        UserEntity user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));
        
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }
        
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = refreshTokenService.create(user.getId());
        
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public RefreshTokenResponse refresh(RefreshTokenRequest request) {
        RefreshTokenRotationResult rotation = refreshTokenService.rotate(request.refreshToken());

        UserEntity user = userRepository.findById(rotation.userId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "User not found"));
        
        if (!user.isEnabled()) {
            throw new BusinessException(ErrorCode.USER_DISABLED);
        }
        
        String accessToken = jwtService.generateAccessToken(user);
        
        
        return RefreshTokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(rotation.newRefreshToken())
                .build();
    }
}
