package org.xuannam.taskflowbackend.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.xuannam.taskflowbackend.AbstractIntegrationTest;
import org.xuannam.taskflowbackend.auth.dto.request.LoginRequest;
import org.xuannam.taskflowbackend.auth.dto.request.RegisterRequest;
import org.xuannam.taskflowbackend.auth.repository.RefreshTokenRepository;
import org.xuannam.taskflowbackend.user.entity.UserEntity;
import org.xuannam.taskflowbackend.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AuthControllerIntegrationTest extends AbstractIntegrationTest {
    
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    RefreshTokenRepository refreshTokenRepository;
    
    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void cleanDatabase() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    UserEntity createDisabledUser() {
        UserEntity user = new UserEntity();
        user.setEmail("nam@gmail.com");
        user.setUsername("xuannam");
        user.setPassword(passwordEncoder.encode("12345678"));
        user.setEnabled(false);
        return userRepository.save(user);
    }

    UserEntity createValidUser() {
        UserEntity user = new UserEntity();
        user.setEmail("nam@gmail.com");
        user.setUsername("xuannam");
        user.setPassword(passwordEncoder.encode("12345678"));
        user.setEnabled(true);
        return userRepository.save(user);
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
                    "Password123"
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
            assertThat(savedUser.getPassword()).isNotEqualTo("Password123");
            assertThat(passwordEncoder.matches("Password123", savedUser.getPassword())).isTrue();
            
        }
        
        @Test
        @DisplayName("should reject duplicate email")
        void shouldRejectDuplicateEmail() throws Exception {
            createValidUser();
            
            RegisterRequest request = new RegisterRequest(
                    "nam@gmail.com",
                    "namdang",
                    "12345678"
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
            createDisabledUser();
            LoginRequest request = new LoginRequest(
                    "nam@gmail.com",
                    "12345678"
            );
            
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpectAll(status().isForbidden(),
                            jsonPath("$.code").value("AUTH_007"));
        }
        
    }
}
