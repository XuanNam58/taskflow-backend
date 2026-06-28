package org.xuannam.taskflowbackend.auth.service;

import org.xuannam.taskflowbackend.auth.dto.request.LoginRequest;
import org.xuannam.taskflowbackend.auth.dto.request.RegisterRequest;
import org.xuannam.taskflowbackend.auth.dto.response.LoginResponse;
import org.xuannam.taskflowbackend.auth.dto.response.RegisterResponse;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
}
