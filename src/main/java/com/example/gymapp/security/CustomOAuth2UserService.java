package com.example.gymapp.security;

import com.example.gymapp.entity.AuthProvider;
import com.example.gymapp.entity.User;
import com.example.gymapp.entity.UserRole;
import com.example.gymapp.entity.UserStats;
import com.example.gymapp.exception.BadRequestException;
import com.example.gymapp.repository.UserRepository;
import com.example.gymapp.repository.UserStatsRepository;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
    private final UserRepository userRepository;
    private final UserStatsRepository userStatsRepository;

    public CustomOAuth2UserService(UserRepository userRepository, UserStatsRepository userStatsRepository) {
        this.userRepository = userRepository;
        this.userStatsRepository = userStatsRepository;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        AuthProvider provider = toProvider(registrationId);

        ProviderUserInfo providerUserInfo = extractProviderUserInfo(provider, oauth2User.getAttributes());

        User user = upsertUser(provider, providerUserInfo);
        ensureStatsExists(user);

        return new CustomOAuth2User(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getProvider(),
                user.getProviderUserId(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
                oauth2User.getAttributes()
        );
    }

    private AuthProvider toProvider(String registrationId) {
        return switch (registrationId.toLowerCase()) {
            case "google" -> AuthProvider.GOOGLE;
            case "facebook" -> AuthProvider.FACEBOOK;
            case "twitter" -> AuthProvider.TWITTER;
            default -> throw new BadRequestException("Unsupported OAuth provider: " + registrationId);
        };
    }

    @SuppressWarnings("unchecked")
    private ProviderUserInfo extractProviderUserInfo(AuthProvider provider, Map<String, Object> attributes) {
        if (provider == AuthProvider.TWITTER) {
            Object dataRaw = attributes.get("data");
            if (!(dataRaw instanceof Map<?, ?> dataMap)) {
                throw new BadRequestException("Invalid Twitter user response");
            }

            String providerUserId = getString(dataMap, "id");
            String name = Optional.ofNullable(getString(dataMap, "name"))
                    .filter(value -> !value.isBlank())
                    .orElse(getString(dataMap, "username"));
            String email = Optional.ofNullable(getString(dataMap, "email"))
                    .filter(value -> !value.isBlank())
                    .orElse(providerUserId + "@twitter.local");
            String pictureUrl = getString(dataMap, "profile_image_url");

            validateRequired(providerUserId, "providerUserId");
            validateRequired(name, "name");

            return new ProviderUserInfo(providerUserId, name, email, pictureUrl);
        }

        String providerUserId = switch (provider) {
            case GOOGLE -> getString(attributes, "sub");
            case FACEBOOK -> getString(attributes, "id");
            default -> null;
        };

        String name = getString(attributes, "name");
        String email = Optional.ofNullable(getString(attributes, "email"))
                .filter(value -> !value.isBlank())
                .orElse(providerUserId + "@" + provider.name().toLowerCase() + ".local");
        String pictureUrl = switch (provider) {
            case GOOGLE -> getString(attributes, "picture");
            case FACEBOOK -> extractFacebookPictureUrl(attributes);
            default -> null;
        };

        validateRequired(providerUserId, "providerUserId");
        validateRequired(name, "name");

        return new ProviderUserInfo(providerUserId, name, email, pictureUrl);
    }

    private User upsertUser(AuthProvider provider, ProviderUserInfo info) {
        Optional<User> existingUser = userRepository.findByProviderAndProviderUserId(provider, info.providerUserId());

        User user = existingUser.orElseGet(User::new);
        if (user.getId() == null) {
            user.setCreatedAt(LocalDateTime.now());
            user.setProvider(provider);
            user.setProviderUserId(info.providerUserId());
            user.setRole(UserRole.USER);
        } else if (user.getRole() == null) {
            user.setRole(UserRole.USER);
        }

        user.setName(info.name());
        user.setEmail(info.email());
        user.setProfileImageUrl(info.profileImageUrl());

        return userRepository.save(user);
    }

    @SuppressWarnings("unchecked")
    private String extractFacebookPictureUrl(Map<String, Object> attributes) {
        Object pictureObj = attributes.get("picture");
        if (!(pictureObj instanceof Map<?, ?> pictureMap)) {
            return null;
        }

        Object dataObj = pictureMap.get("data");
        if (!(dataObj instanceof Map<?, ?> dataMap)) {
            return null;
        }

        return getString(dataMap, "url");
    }

    private void ensureStatsExists(User user) {
        if (userStatsRepository.findByUser(user).isPresent()) {
            return;
        }

        UserStats stats = new UserStats();
        stats.setUser(user);
        stats.setCurrentStreak(0);
        stats.setLongestStreak(0);
        stats.setLastVisitDate(null);
        userStatsRepository.save(stats);
    }

    private String getString(Map<?, ?> attributes, String key) {
        Object value = attributes.get(key);
        return value == null ? null : String.valueOf(value);
    }

    private void validateRequired(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new BadRequestException("OAuth attribute missing: " + field);
        }
    }

    private record ProviderUserInfo(String providerUserId, String name, String email, String profileImageUrl) {
    }
}
