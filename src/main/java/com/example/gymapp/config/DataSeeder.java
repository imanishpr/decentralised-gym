package com.example.gymapp.config;

import com.example.gymapp.entity.AuthProvider;
import com.example.gymapp.entity.Gym;
import com.example.gymapp.entity.User;
import com.example.gymapp.entity.UserRole;
import com.example.gymapp.entity.UserStats;
import com.example.gymapp.entity.VisitCode;
import com.example.gymapp.repository.GymRepository;
import com.example.gymapp.repository.UserRepository;
import com.example.gymapp.repository.UserStatsRepository;
import com.example.gymapp.repository.VisitCodeRepository;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    @Bean
    CommandLineRunner seedPhaseOneData(
            GymRepository gymRepository,
            VisitCodeRepository visitCodeRepository,
            UserRepository userRepository,
            UserStatsRepository userStatsRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            Gym downtown = ensureGym(
                    gymRepository,
                    "Iron Temple Downtown",
                    "101 Main St",
                    "Austin",
                    true,
                    30.2672,
                    -97.7431,
                    "https://maps.google.com/?q=30.2672,-97.7431",
                    "https://images.unsplash.com/photo-1534438327276-14e5300c3a48",
                    299.0
            );
            Gym riverside = ensureGym(
                    gymRepository,
                    "Riverside Strength Club",
                    "222 Lake Ave",
                    "Austin",
                    true,
                    30.2500,
                    -97.7100,
                    "https://maps.google.com/?q=30.2500,-97.7100",
                    "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b",
                    249.0
            );
            Gym metro = ensureGym(
                    gymRepository,
                    "MetroFit Central",
                    "88 Market Blvd",
                    "Dallas",
                    true,
                    32.7767,
                    -96.7970,
                    "https://maps.google.com/?q=32.7767,-96.7970",
                    "https://images.unsplash.com/photo-1517836357463-d25dfeac3438",
                    199.0
            );

            seedCodes(visitCodeRepository, downtown, List.of(
                    "AUS-DT-1001", "AUS-DT-1002", "AUS-DT-1003", "AUS-DT-1004", "AUS-DT-1005"
            ));
            seedCodes(visitCodeRepository, riverside, List.of(
                    "AUS-RV-2001", "AUS-RV-2002", "AUS-RV-2003", "AUS-RV-2004", "AUS-RV-2005"
            ));
            seedCodes(visitCodeRepository, metro, List.of(
                    "DAL-MT-3001", "DAL-MT-3002", "DAL-MT-3003", "DAL-MT-3004", "DAL-MT-3005"
            ));

            User seededUser = ensureLocalUser(
                    userRepository,
                    passwordEncoder,
                    "Demo User",
                    "user1@gymapp.com",
                    "User@12345",
                    UserRole.USER
            );
            User seededAdmin = ensureLocalUser(
                    userRepository,
                    passwordEncoder,
                    "Demo Admin",
                    "admin1@gymapp.com",
                    "Admin@12345",
                    UserRole.ADMIN
            );
            ensureUserStats(userStatsRepository, seededUser);
            ensureUserStats(userStatsRepository, seededAdmin);

            log.info(
                    "Seed complete: gyms={}, visitCodes={}, users={}",
                    gymRepository.count(),
                    visitCodeRepository.count(),
                    userRepository.count()
            );
        };
    }

    private Gym ensureGym(
            GymRepository gymRepository,
            String name,
            String address,
            String city,
            boolean active,
            Double latitude,
            Double longitude,
            String googleMapUrl,
            String imageUrl,
            Double pricePerHourInr
    ) {
        return gymRepository.findByNameAndCity(name, city)
                .map(existing -> {
                    boolean changed = false;
                    if (existing.getMaxDailyVisits() == null) {
                        existing.setMaxDailyVisits(1000);
                        changed = true;
                    }
                    if (existing.getActiveFromTime() == null) {
                        existing.setActiveFromTime(LocalTime.of(5, 0));
                        changed = true;
                    }
                    if (existing.getActiveToTime() == null) {
                        existing.setActiveToTime(LocalTime.of(23, 0));
                        changed = true;
                    }
                    if (existing.getLatitude() == null) {
                        existing.setLatitude(latitude);
                        changed = true;
                    }
                    if (existing.getLongitude() == null) {
                        existing.setLongitude(longitude);
                        changed = true;
                    }
                    if (existing.getGoogleMapUrl() == null || existing.getGoogleMapUrl().isBlank()) {
                        existing.setGoogleMapUrl(googleMapUrl);
                        changed = true;
                    }
                    if (existing.getImageUrl() == null || existing.getImageUrl().isBlank()) {
                        existing.setImageUrl(imageUrl);
                        changed = true;
                    }
                    if (existing.getPricePerHourInr() == null) {
                        existing.setPricePerHourInr(pricePerHourInr);
                        changed = true;
                    }
                    return changed ? gymRepository.save(existing) : existing;
                })
                .orElseGet(() -> {
                    Gym gym = new Gym();
                    gym.setName(name);
                    gym.setAddress(address);
                    gym.setCity(city);
                    gym.setLatitude(latitude);
                    gym.setLongitude(longitude);
                    gym.setGoogleMapUrl(googleMapUrl);
                    gym.setImageUrl(imageUrl);
                    gym.setPricePerHourInr(pricePerHourInr);
                    gym.setActive(active);
                    gym.setMaxDailyVisits(1000);
                    gym.setActiveFromTime(LocalTime.of(5, 0));
                    gym.setActiveToTime(LocalTime.of(23, 0));
                    return gymRepository.save(gym);
                });
    }

    private void seedCodes(VisitCodeRepository visitCodeRepository, Gym gym, List<String> codes) {
        for (String rawCode : codes) {
            if (visitCodeRepository.existsByCode(rawCode)) {
                continue;
            }
            VisitCode code = new VisitCode();
            code.setCode(rawCode);
            code.setGym(gym);
            code.setUsed(false);
            code.setIssuedToGymAt(LocalDateTime.now());
            code.setUsedByUser(null);
            code.setUsedAt(null);
            visitCodeRepository.save(code);
        }
    }

    private User ensureLocalUser(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            String name,
            String email,
            String rawPassword,
            UserRole role
    ) {
        return userRepository.findByEmail(email)
                .map(existing -> {
                    boolean changed = false;
                    if (existing.getProvider() == null || existing.getProvider() == AuthProvider.LOCAL) {
                        if (existing.getProvider() == null) {
                            existing.setProvider(AuthProvider.LOCAL);
                            changed = true;
                        }
                        if (existing.getProviderUserId() == null || existing.getProviderUserId().isBlank()) {
                            existing.setProviderUserId(email);
                            changed = true;
                        }
                        if (existing.getPasswordHash() == null || existing.getPasswordHash().isBlank()) {
                            existing.setPasswordHash(passwordEncoder.encode(rawPassword));
                            changed = true;
                        }
                    } else {
                        log.warn("Seed user {} exists as social provider ({}), skipping password override", email, existing.getProvider());
                    }
                    if (existing.getRole() != role) {
                        existing.setRole(role);
                        changed = true;
                    }
                    if (existing.getCreatedAt() == null) {
                        existing.setCreatedAt(LocalDateTime.now());
                        changed = true;
                    }
                    if (existing.getName() == null || existing.getName().isBlank()) {
                        existing.setName(name);
                        changed = true;
                    }
                    return changed ? userRepository.save(existing) : existing;
                })
                .orElseGet(() -> {
                    User user = new User();
                    user.setName(name);
                    user.setEmail(email);
                    user.setProvider(AuthProvider.LOCAL);
                    user.setProviderUserId(email);
                    user.setPasswordHash(passwordEncoder.encode(rawPassword));
                    user.setRole(role);
                    user.setCreatedAt(LocalDateTime.now());
                    return userRepository.save(user);
                });
    }

    private void ensureUserStats(UserStatsRepository userStatsRepository, User user) {
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
}
