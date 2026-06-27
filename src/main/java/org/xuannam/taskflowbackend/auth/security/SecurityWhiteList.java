package org.xuannam.taskflowbackend.auth.security;

public final class SecurityWhiteList {
    
    public static final String[] PUBLIC_ENDPOINTS = {
            "/api/auth/register",
            "/api/auth/login",
            "/api/auth/refresh"
    };
}
