package org.xuannam.taskflowbackend.auth;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MvcResult;
import org.xuannam.taskflowbackend.AbstractIntegrationTest;
import org.xuannam.taskflowbackend.auth.dto.request.LoginRequest;
import org.xuannam.taskflowbackend.auth.dto.request.RegisterRequest;
import org.xuannam.taskflowbackend.auth.repository.RefreshTokenRepository;
import org.xuannam.taskflowbackend.auth.security.JwtProperties;
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

    @Autowired
    JwtProperties jwtProperties;

    private static final String PASSWORD = "12345678";

    UserEntity createUser(String email, String username, boolean enabled) {
        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(PASSWORD));
        user.setEnabled(enabled);
        return userRepository.save(user);
    }

    record Tokens(String accessToken, String refreshToken) {
    }

    Tokens login(String email) throws Exception {
        MockHttpServletResponse response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(email, PASSWORD))))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("refresh_token"))
                .andReturn().getResponse();

        return new Tokens(
                objectMapper.readTree(response.getContentAsString()).get("accessToken").asText(),
                response.getCookie("refresh_token").getValue()
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
        @DisplayName("should return access token and refresh token for valid user")
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
                            jsonPath("$.refreshToken").doesNotExist(),
                            cookie().exists("refresh_token"),
                            cookie().httpOnly("refresh_token", true),
                            cookie().path("refresh_token", "/api/auth"),
                            cookie().secure("refresh_token", true),
                            cookie().sameSite("refresh_token", "Strict"),
                            cookie().maxAge("refresh_token", (int) jwtProperties.refreshTokenExpiration().toSeconds()));
        }
    }

    @Nested
    @DisplayName("POST /api/auth/refresh")
    class RefreshToken {

        @Test
        @DisplayName("should return new access token and set new refresh token in cookie")
        void shouldReturnNewPairedToken() throws Exception {
            createUser("nam@gmail.com", "xuannam", true);
            Tokens nam = login("nam@gmail.com");

            String refreshToken = nam.refreshToken();

            MvcResult result = mockMvc.perform(post("/api/auth/refresh")
                            .cookie(new Cookie("refresh_token", refreshToken)))
                    .andExpectAll(status().isOk(),
                            jsonPath("$.accessToken").isNotEmpty(),
                            jsonPath("$.refreshToken").doesNotExist(),
                            cookie().exists("refresh_token"))
                    .andReturn();

            String newRefreshToken = result.getResponse().getCookie("refresh_token").getValue();

            RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.findByToken(refreshToken).orElseThrow();

            assertThat(refreshTokenEntity.isRevoked()).isTrue();
            assertThat(newRefreshToken).isNotEqualTo(refreshToken);
        }

        @Test
        @DisplayName("should return 401 AUTH_004 when cookie is missing")
        void shouldReturn401WhenCookieIsMissing() throws Exception {
            mockMvc.perform(post("/api/auth/refresh"))
                    .andExpectAll(status().isUnauthorized(),
                            jsonPath("$.code").value("AUTH_004"));
        }

        @Test
        @DisplayName("should return 403 AUTH_007 when user is disabled")
        void shouldReturn403WhenUserDisabled() throws Exception {
            createUser("nam@gmail.com", "xuannam", true);
            Tokens nam = login("nam@gmail.com");

            UserEntity user = userRepository.findByEmail("nam@gmail.com").orElseThrow();
            user.setEnabled(false);
            userRepository.save(user);

            String refreshToken = nam.refreshToken();

            mockMvc.perform(post("/api/auth/refresh")
                            .cookie(new Cookie("refresh_token", refreshToken)))
                    .andExpectAll(status().isForbidden(),
                            jsonPath("$.code").value("AUTH_007"));

            RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.findByToken(refreshToken).orElseThrow();

            assertThat(refreshTokenEntity.isRevoked()).isFalse();
        }

        @Test
        @DisplayName("should return 401 AUTH_006 when reuse cookie")
        void shouldReturn401WhenReuseCookie() throws Exception {
            createUser("nam@gmail.com", "xuannam", true);
            Tokens nam = login("nam@gmail.com");

            String refreshToken = nam.refreshToken();

            mockMvc.perform(post("/api/auth/refresh")
                            .cookie(new Cookie("refresh_token", refreshToken)))
                    .andExpectAll(status().isOk());

            mockMvc.perform(post("/api/auth/refresh")
                            .cookie(new Cookie("refresh_token", refreshToken)))
                    .andExpectAll(status().isUnauthorized(),
                            jsonPath("$.code").value("AUTH_006"));
        }
    }

    @Nested
    @DisplayName("POST /api/auth/logout")
    class Logout {
        @Test
        @DisplayName("should clear refresh cookie on logout")
        void shouldClearRefreshCookieOnLogout() throws Exception {
            createUser("nam@gmail.com", "xuannam", true);
            Tokens nam = login("nam@gmail.com");

            String accessToken = nam.accessToken();
            String refreshToken = nam.refreshToken();

            mockMvc.perform(post("/api/auth/logout")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                            .cookie(new Cookie("refresh_token", refreshToken)))
                    .andExpectAll(status().isNoContent(),
                            cookie().maxAge("refresh_token", 0),
                            cookie().path("refresh_token", "/api/auth"));
        }

        @Test
        @DisplayName("should reject revoked token")
        void shouldRejectRevokedToken() throws Exception {
            createUser("nam@gmail.com", "xuannam", true);
            Tokens nam = login("nam@gmail.com");

            String accessToken = nam.accessToken();
            String refreshToken = nam.refreshToken();

            mockMvc.perform(post("/api/auth/logout")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                            .cookie(new Cookie("refresh_token", refreshToken)))
                    .andExpect(status().isNoContent());

            mockMvc.perform(post("/api/auth/refresh")
                            .cookie(new Cookie("refresh_token", refreshToken)))
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
                            .cookie(new Cookie("refresh_token", nam1.refreshToken())))
                    .andExpectAll(status().isUnauthorized(),
                            jsonPath("$.code").value("AUTH_004"));
        }
    }

    @Nested
    @DisplayName("POST /api/auth/logout-all")
    class LogoutAll {
        @Test
        @DisplayName("should return 204 when logout all")
        void shouldReturn204WhenLogoutAll() throws Exception {
            createUser("nam@gmail.com", "xuannam", true);
            Tokens nam = login("nam@gmail.com");

            String accessToken = nam.accessToken();

            mockMvc.perform(post("/api/auth/logout-all")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                    .andExpectAll(status().isNoContent(),
                            cookie().maxAge("refresh_token", 0),
                            cookie().path("refresh_token", "/api/auth"));
        }
    }
}
