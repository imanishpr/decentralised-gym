package com.example.gymapp.security;

public class JwtAuthenticatedUser {

    private final Long userId;

    public JwtAuthenticatedUser(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }
}
