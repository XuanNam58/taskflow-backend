package org.xuannam.taskflowbackend.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.xuannam.taskflowbackend.auth.entity.RefreshTokenEntity;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByToken(String token);
    List<RefreshTokenEntity> findByUserIdAndRevokedFalse(Long userId);
}