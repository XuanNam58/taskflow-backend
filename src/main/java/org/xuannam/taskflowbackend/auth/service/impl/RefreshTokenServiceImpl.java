package org.xuannam.taskflowbackend.auth.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xuannam.taskflowbackend.auth.dto.RefreshTokenRotationResult;
import org.xuannam.taskflowbackend.auth.entity.RefreshTokenEntity;
import org.xuannam.taskflowbackend.auth.repository.RefreshTokenRepository;
import org.xuannam.taskflowbackend.auth.security.JwtProperties;
import org.xuannam.taskflowbackend.auth.service.RefreshTokenService;
import org.xuannam.taskflowbackend.common.exception.BusinessException;
import org.xuannam.taskflowbackend.common.exception.ErrorCode;

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
    
    @Transactional
    @Override
    public String create(Long userId) {
        return createAndSave(userId);
    }

    private String createAndSave(Long userId) {
        String token = generateToken();

        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setUserId(userId);
        entity.setToken(token);
        entity.setExpiresAt(
                Instant.now().plus(jwtProperties.refreshTokenExpiration())
        );
        entity.setRevoked(false);

        refreshTokenRepository.save(entity);
        return token;
    }

    @Transactional
    @Override
    public RefreshTokenRotationResult rotate(String token) {
        RefreshTokenEntity currentToken = refreshTokenRepository.findByTokenForUpdate(token)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN));

        if (currentToken.isRevoked()) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_REVOKED);
        }

        if (!currentToken.getExpiresAt().isAfter(Instant.now())) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }
        
        currentToken.setRevoked(true);
        
        String newToken = createAndSave(currentToken.getUserId());
        return new RefreshTokenRotationResult(
                currentToken.getUserId(), 
                newToken
        );
    }

    private String generateToken() {
        byte[] bytes = new byte[64];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    @Transactional
    @Override
    public void revoke(String token) {
        RefreshTokenEntity refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN));
        
        if (refreshToken.isRevoked()) {
            return;
        }
        
        refreshToken.setRevoked(true);
    }

    @Override
    public void revokeAll(Long userId) {

    }
}
