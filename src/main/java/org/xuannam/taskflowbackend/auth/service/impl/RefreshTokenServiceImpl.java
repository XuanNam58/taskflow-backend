package org.xuannam.taskflowbackend.auth.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.xuannam.taskflowbackend.auth.entity.RefreshTokenEntity;
import org.xuannam.taskflowbackend.auth.repository.RefreshTokenRepository;
import org.xuannam.taskflowbackend.auth.security.JwtProperties;
import org.xuannam.taskflowbackend.auth.service.RefreshTokenService;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RefreshTokenServiceImpl implements RefreshTokenService {
    
    RefreshTokenRepository refreshTokenRepository;
    JwtProperties jwtProperties;
    SecureRandom secureRandom = new SecureRandom();
    
    @Override
    public String create(Long userId) {
        String token = generateToken();
        
        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setUserId(userId);
        entity.setToken(token);
        entity.setExpiresAt(Instant.now().plus(jwtProperties.refreshTokenExpiration()));
        entity.setRevoked(false);
        
        refreshTokenRepository.save(entity);
        return token;
    }
    
    private String generateToken() {
        byte[] bytes = new byte[64];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    @Override
    public RefreshTokenEntity validate(String token) {
        return null;
    }

    @Override
    public void revoke(String token) {

    }

    @Override
    public void revokeAll(Long userId) {

    }
}
