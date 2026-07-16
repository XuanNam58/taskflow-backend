package org.xuannam.taskflowbackend.auth.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.xuannam.taskflowbackend.auth.entity.RefreshTokenEntity;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByToken(String token);
    List<RefreshTokenEntity> findByUserIdAndRevokedFalse(Long userId);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select token 
            from RefreshTokenEntity token
            where token.token = :token           
            """)
    Optional<RefreshTokenEntity> findByTokenForUpdate(String token);
}