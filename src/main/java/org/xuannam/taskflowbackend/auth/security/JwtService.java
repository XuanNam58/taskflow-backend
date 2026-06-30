package org.xuannam.taskflowbackend.auth.security;

import org.xuannam.taskflowbackend.user.entity.UserEntity;

public interface JwtService {
    String generateAccessToken(UserEntity user);
    String generateRefreshToken();
    boolean validateToken(String token);
    Long getUserIdFromToken(String token);
}
