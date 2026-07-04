package org.xuannam.taskflowbackend.auth.security;

import org.xuannam.taskflowbackend.user.entity.UserEntity;

public interface JwtService {
    String generateAccessToken(UserEntity user);
    boolean validateAccessToken(String token);
    Long extractUserId(String token);
}
