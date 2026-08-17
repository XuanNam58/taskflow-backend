package org.xuannam.taskflowbackend.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.xuannam.taskflowbackend.AbstractIntegrationTest;
import org.xuannam.taskflowbackend.auth.dto.request.LoginRequest;
import org.xuannam.taskflowbackend.auth.dto.request.LogoutRequest;
import org.xuannam.taskflowbackend.auth.dto.request.RefreshTokenRequest;
import org.xuannam.taskflowbackend.auth.dto.request.RegisterRequest;
import org.xuannam.taskflowbackend.auth.repository.RefreshTokenRepository;
import org.xuannam.taskflowbackend.user.entity.UserEntity;
import org.xuannam.taskflowbackend.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.xuannam.taskflowbackend.auth.entity.RefreshTokenEntity;

public class AuthControllerIntegrationTest extends AbstractIntegrationTest {
    
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    RefreshTokenRepository refreshTokenRepository;
    
    @Autowired
    PasswordEncoder passwordEncoder;
    
    private static final String PASSWORD = "12345678";
    
    UserEntity createUser(String email, String username, boolean enabled) {
        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(PASSWORD));
        user.setEnabled(enabled);
        return userRepository.save(user);
    }
    
    record Tokens(String accessToken, String refreshToken) {}
    
    Tokens login(String email) throws Exception {
        String body = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest(email, PASSWORD))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        return new Tokens(
                objectMapper.readTree(body).get("accessToken").asText(),
                objectMapper.readTree(body).get("refreshToken").asText()
        );
    }

    @BeforeEach
    void cleanDatabase() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
    }
    
    @Nested
    @DisplayName("POST /api/auth/register")
    class Register {
        
        @Test
        @DisplayName("should register a new user")
        void shouldRegisterNewUser() throws Exception {
            RegisterRequest request = new RegisterRequest(
                    "nam@example.com",
                    "xuannam",
                    PASSWORD
            );
            
            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentTypeCompatibleWith(
                            MediaType.APPLICATION_JSON
                    ))
                    .andExpect(jsonPath("$.id").isNumber())
                    .andExpect(jsonPath("$.email").value("nam@example.com"))
                    .andExpect(jsonPath("$.username").value("xuannam"));

            UserEntity savedUser = userRepository
                    .findByEmail("nam@example.com")
                    .orElseThrow();
            
            assertThat(savedUser.getUsername()).isEqualTo("xuannam");
            assertThat(savedUser.isEnabled()).isTrue();
            assertThat(savedUser.getPassword()).isNotEqualTo(PASSWORD);
            assertThat(passwordEncoder.matches(PASSWORD, savedUser.getPassword())).isTrue();
            
        }
        
        @Test
        @DisplayName("should reject duplicate email")
        void shouldRejectDuplicateEmail() throws Exception {
            createUser("nam@gmail.com", "xuannam", true);
            
            RegisterRequest request = new RegisterRequest(
                    "nam@gmail.com",
                    "namdang",
                    PASSWORD
            );
            
            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.code")
                            .value("USER_001"));
        }
    }
    
    @Nested
    @DisplayName("POST /api/auth/login")
    class Login {
        
        @Test
        @DisplayName("should reject disabled user")
        void shouldRejectDisabledUser() throws Exception {
            createUser("nam@gmail.com", "xuannam", false);
            LoginRequest request = new LoginRequest(
                    "nam@gmail.com",
                    PASSWORD
            );
            
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpectAll(status().isForbidden(),
                            jsonPath("$.code").value("AUTH_007"));
        }
        
        @Test
        @DisplayName("should  return access token and refresh token for valid user")
        void shouldReturnAccessTokenAndRefreshTokenForValidUser() throws Exception {
            createUser("nam@gmail.com", "xuannam", true);
            LoginRequest request = new LoginRequest(
                    "nam@gmail.com",
                    PASSWORD
            );

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpectAll(status().isOk(),
                            jsonPath("$.accessToken").isNotEmpty(),
                            jsonPath("$.refreshToken").isNotEmpty());
        }
    }

    @Nested
    @DisplayName("POST /api/auth/refresh")
    class RefreshToken {

        @Test
        @DisplayName("should return new paired token and revoke old refresh token")
        void shouldReturnNewPairedToken() throws Exception {
            createUser("nam@gmail.com", "xuannam", true);
            Tokens nam = login("nam@gmail.com");

            String refreshToken = nam.refreshToken();

            mockMvc.perform(post("/api/auth/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new RefreshTokenRequest(refreshToken))))
                    .andExpectAll(status().isOk(),
                            jsonPath("$.accessToken").isNotEmpty(),
                            jsonPath("$.refreshToken").isNotEmpty());

            RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.findByToken(refreshToken).orElseThrow();

            assertThat(refreshTokenEntity.isRevoked()).isTrue();
        }

        @Test
        @DisplayName("should throw error when user is disabled")
        void shouldThrowErrorWhenUserDisabled() throws Exception {
            createUser("nam@gmail.com", "xuannam", true);
            Tokens nam = login("nam@gmail.com");
            
            UserEntity user = userRepository.findByEmail("nam@gmail.com").orElseThrow();
            user.setEnabled(false);
            userRepository.save(user);

            String refreshToken = nam.refreshToken();

            mockMvc.perform(post("/api/auth/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new RefreshTokenRequest(refreshToken))))
                    .andExpectAll(status().isForbidden(),
                            jsonPath("$.code").value("AUTH_007"));

            RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.findByToken(refreshToken).orElseThrow();

            assertThat(refreshTokenEntity.isRevoked()).isFalse();
        }
    }

    @Nested
    @DisplayName("POST /api/auth/logout")
    class Logout {
        @Test
        @DisplayName("should reject revoked token")
        void shouldRejectRevokedToken() throws Exception {
            createUser("nam@gmail.com", "xuannam", true);
            Tokens nam = login("nam@gmail.com");

            String accessToken = nam.accessToken();
            String refreshToken = nam.refreshToken();

            mockMvc.perform(post("/api/auth/logout")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new LogoutRequest(refreshToken))))
                    .andExpect(status().isNoContent());

            mockMvc.perform(post("/api/auth/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new RefreshTokenRequest(refreshToken))))
                    .andExpectAll(status().isUnauthorized(),
                            jsonPath("$.code").value("AUTH_006"));
        }

        @Test
        @DisplayName("should prevent revoke token by another user")
        void shouldPreventRevokeTokenByAnotherUser() throws Exception {
            createUser("nam1@gmail.com", "xuannam1", true);
            createUser("nam2@gmail.com", "xuannam2", true);

            Tokens nam1 = login("nam1@gmail.com");
            Tokens nam2 = login("nam2@gmail.com");

            mockMvc.perform(post("/api/auth/logout")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + nam2.accessToken())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new LogoutRequest(nam1.refreshToken()))))
                    .andExpectAll(status().isUnauthorized(),
                            jsonPath("$.code").value("AUTH_004"));
        }
    }
}
