package org.xuannam.taskflowbackend.user.service;

import org.xuannam.taskflowbackend.user.dto.response.UserResponse;

public interface UserService {
    UserResponse getProfile(Long userId);
}
