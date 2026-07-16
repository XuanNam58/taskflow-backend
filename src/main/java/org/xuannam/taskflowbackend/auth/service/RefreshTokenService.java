package org.xuannam.taskflowbackend.auth.service;

import org.xuannam.taskflowbackend.auth.dto.RefreshTokenRotationResult;
import org.xuannam.taskflowbackend.auth.entity.RefreshTokenEntity;

public interface RefreshTokenService {
    String create(Long userId);
    RefreshTokenRotationResult rotate(String token);
    void revoke(String token);
    void revokeAll(Long userId);
}
