package org.xuannam.taskflowbackend.auth.security;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.xuannam.taskflowbackend.user.entity.UserEntity;
import org.xuannam.taskflowbackend.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticatedUserService {
    UserRepository userRepository;
    
    public AuthenticatedUser loadById(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        if (!user.isEnabled()) {
            throw new DisabledException("User is disabled");
        }
        
        return new AuthenticatedUser(userId);
    }
}
