package com.example.gymapp.service;

import com.example.gymapp.dto.AuthResponse;
import com.example.gymapp.dto.LoginRequest;
import com.example.gymapp.dto.SignupRequest;
import com.example.gymapp.dto.UserProfileResponse;
import com.example.gymapp.entity.AuthProvider;
import com.example.gymapp.entity.User;
import com.example.gymapp.entity.UserRole;
import com.example.gymapp.entity.UserStats;
import com.example.gymapp.exception.BadRequestException;
import com.example.gymapp.exception.ConflictException;
import com.example.gymapp.exception.UnauthorizedException;
import com.example.gymapp.repository.UserRepository;
import com.example.gymapp.repository.UserStatsRepository;
import com.example.gymapp.security.JwtService;
import java.time.LocalDateTime;
import com.example.gymapp.util.AuthenticatedUserUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final AuthenticatedUserUtil authenticatedUserUtil;
    private final UserRepository userRepository;
    private final UserStatsRepository userStatsRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            AuthenticatedUserUtil authenticatedUserUtil,
            UserRepository userRepository,
            UserStatsRepository userStatsRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.authenticatedUserUtil = authenticatedUserUtil;
        this.userRepository = userRepository;
        this.userStatsRepository = userStatsRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse signup(SignupRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        String name = request.getName().trim();

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("password and confirmPassword do not match");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new ConflictException("A user with this email already exists");
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setProvider(AuthProvider.LOCAL);
        user.setProviderUserId(email);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.USER);
        user.setCreatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        UserStats stats = new UserStats();
        stats.setUser(savedUser);
        stats.setCurrentStreak(0);
        stats.setLongestStreak(0);
        stats.setLastVisitDate(null);
        userStatsRepository.save(stats);

        return buildAuthResponse("Signup successful", savedUser);
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (user.getProvider() != AuthProvider.LOCAL || user.getPasswordHash() == null) {
            throw new BadRequestException("This account uses social login. Use OAuth login instead.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        ensureRoleAssigned(user);
        return buildAuthResponse("Login successful", user);
    }

    public UserProfileResponse getCurrentUserProfile() {
        User user = authenticatedUserUtil.getCurrentUser();
        ensureRoleAssigned(user);
        return toProfile(user);
    }

    public AuthResponse getCurrentUserAuthResponse(String message) {
        User user = authenticatedUserUtil.getCurrentUser();
        ensureRoleAssigned(user);
        return buildAuthResponse(message, user);
    }

    private UserProfileResponse toProfile(User user) {
        UserProfileResponse response = new UserProfileResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setProvider(user.getProvider());
        response.setRole(user.getRole());
        response.setProviderUserId(user.getProviderUserId());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }

    private AuthResponse buildAuthResponse(String message, User user) {
        String token = jwtService.generateToken(user.getId(), user.getEmail());
        AuthResponse response = new AuthResponse();
        response.setMessage(message);
        response.setToken(token);
        response.setTokenType("Bearer");
        response.setExpiresInSeconds(jwtService.getExpirationSeconds());
        response.setUser(toProfile(user));
        return response;
    }

    private void ensureRoleAssigned(User user) {
        if (user.getRole() == null) {
            user.setRole(UserRole.USER);
            userRepository.save(user);
        }
    }
}
