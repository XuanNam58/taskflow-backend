package org.xuannam.taskflowbackend.user.mapper;

import org.mapstruct.Mapper;
import org.xuannam.taskflowbackend.auth.dto.response.RegisterResponse;
import org.xuannam.taskflowbackend.user.dto.response.UserResponse;
import org.xuannam.taskflowbackend.user.entity.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {
    RegisterResponse toRegisterResponse(UserEntity user);
    UserResponse toUserResponse(UserEntity entity);
}
