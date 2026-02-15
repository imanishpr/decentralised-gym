package com.example.gymapp.security;

import com.example.gymapp.entity.AuthProvider;
import java.util.Collection;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class CustomOAuth2User implements OAuth2User {

    private final Long userId;
    private final String email;
    private final String name;
    private final AuthProvider provider;
    private final String providerUserId;
    private final Collection<? extends GrantedAuthority> authorities;
    private final Map<String, Object> attributes;

    public CustomOAuth2User(
            Long userId,
            String email,
            String name,
            AuthProvider provider,
            String providerUserId,
            Collection<? extends GrantedAuthority> authorities,
            Map<String, Object> attributes
    ) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.provider = provider;
        this.providerUserId = providerUserId;
        this.authorities = authorities;
        this.attributes = attributes;
    }

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public AuthProvider getProvider() {
        return provider;
    }

    public String getProviderUserId() {
        return providerUserId;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return name;
    }
}
