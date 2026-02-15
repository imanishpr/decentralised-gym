package com.example.gymapp.controller;

import com.example.gymapp.dto.AuthResponse;
import com.example.gymapp.dto.LoginRequest;
import com.example.gymapp.dto.SignupRequest;
import com.example.gymapp.dto.UserProfileResponse;
import com.example.gymapp.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(security = {})
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @Operation(security = {})
    @PostMapping("/signup")
    public AuthResponse signup(@Valid @RequestBody SignupRequest request) {
        return authService.signup(request);
    }

    @GetMapping("/login/success")
    public AuthResponse loginSuccess() {
        return authService.getCurrentUserAuthResponse("Social login successful");
    }

    @GetMapping("/me")
    public UserProfileResponse me() {
        return authService.getCurrentUserProfile();
    }
}
