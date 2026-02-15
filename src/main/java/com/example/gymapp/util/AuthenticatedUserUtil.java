package com.example.gymapp.util;

import com.example.gymapp.entity.User;
import com.example.gymapp.exception.UnauthorizedException;
import com.example.gymapp.repository.UserRepository;
import com.example.gymapp.security.CustomOAuth2User;
import com.example.gymapp.security.JwtAuthenticatedUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUserUtil {

    private final UserRepository userRepository;

    public AuthenticatedUserUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new UnauthorizedException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();
        Long userId;
        if (principal instanceof CustomOAuth2User oauthUser) {
            userId = oauthUser.getUserId();
        } else if (principal instanceof JwtAuthenticatedUser jwtUser) {
            userId = jwtUser.getUserId();
        } else {
            throw new UnauthorizedException("User is not authenticated");
        }

        return userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("Authenticated user no longer exists"));
    }
}
