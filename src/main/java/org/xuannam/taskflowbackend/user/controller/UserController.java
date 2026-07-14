package org.xuannam.taskflowbackend.user.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xuannam.taskflowbackend.auth.security.AuthenticatedUser;
import org.xuannam.taskflowbackend.user.dto.response.UserResponse;
import org.xuannam.taskflowbackend.user.service.UserService;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/users")
public class UserController {
    
    UserService userService;
    
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getProfile(@AuthenticationPrincipal AuthenticatedUser userDetails) {
        return ResponseEntity.ok(userService.getProfile(userDetails.userId()));    
    }
    
}
