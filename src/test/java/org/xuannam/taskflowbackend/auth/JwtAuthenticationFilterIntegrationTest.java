package org.xuannam.taskflowbackend.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.xuannam.taskflowbackend.AbstractIntegrationTest;
import org.xuannam.taskflowbackend.auth.repository.RefreshTokenRepository;
import org.xuannam.taskflowbackend.auth.security.JwtProperties;
import org.xuannam.taskflowbackend.auth.security.JwtService;
import org.xuannam.taskflowbackend.auth.security.JwtServiceImpl;
import org.xuannam.taskflowbackend.user.entity.UserEntity;
import org.xuannam.taskflowbackend.user.repository.UserRepository;

import java.time.Duration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class JwtAuthenticationFilterIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    RefreshTokenRepository refreshTokenRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    JwtProperties jwtProperties;
    @BeforeEach
    void cleanDatabase() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    UserEntity createUser() {
        UserEntity user = new UserEntity();
        user.setEmail("nam@gmail.com");
        user.setUsername("xuannam");
        user.setPassword(passwordEncoder.encode("12345678"));
        user.setEnabled(true);
        return userRepository.save(user);
    }

    @Nested
    @DisplayName("GET /api/users/me")
    class GetProfile {
        @Test
        @DisplayName("should return /api/users/me in path")
        void shouldRejectExpiredAccessToken() throws Exception {

            UserEntity user = createUser();
            JwtProperties jwtPropertiesWithShortExpiration = new JwtProperties(
                    jwtProperties.secret(),
                    Duration.ofSeconds(-1),
                    jwtProperties.refreshTokenExpiration()
            );

            JwtService jwtService = new JwtServiceImpl(jwtPropertiesWithShortExpiration);
            String accessToken = jwtService.generateAccessToken(user);
            mockMvc.perform(get("/api/users/me")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpectAll(
                            status().isUnauthorized(),
                            jsonPath("$.code").value("AUTH_003"),
                            jsonPath("$.path").value("/api/users/me")
                    );
        }
    }
}
