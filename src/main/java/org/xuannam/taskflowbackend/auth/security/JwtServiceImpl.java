package org.xuannam.taskflowbackend.auth.security;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.xuannam.taskflowbackend.user.entity.UserEntity;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtServiceImpl implements JwtService{
    @Override
    public String generateAccessToken(UserEntity user) {
        return "";
    }

    @Override
    public String generateRefreshToken() {
        return "";
    }

    @Override
    public boolean validateToken(String token) {
        return false;
    }

    @Override
    public Long getUserIdFromToken(String token) {
        return 0L;
    }
}
