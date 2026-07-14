package org.xuannam.taskflowbackend.user.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.xuannam.taskflowbackend.user.dto.response.UserResponse;
import org.xuannam.taskflowbackend.user.entity.UserEntity;
import org.xuannam.taskflowbackend.user.mapper.UserMapper;
import org.xuannam.taskflowbackend.user.repository.UserRepository;
import org.xuannam.taskflowbackend.user.service.UserService;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    @Override
    public UserResponse getProfile(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return userMapper.toUserResponse(user);
    }
}
