package com.example.gymapp.config;

import com.example.gymapp.entity.Gym;
import com.example.gymapp.entity.VisitCode;
import com.example.gymapp.repository.GymRepository;
import com.example.gymapp.repository.VisitCodeRepository;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    @Bean
    CommandLineRunner seedPhaseOneData(GymRepository gymRepository, VisitCodeRepository visitCodeRepository) {
        return args -> {
            Gym downtown = ensureGym(gymRepository, "Iron Temple Downtown", "101 Main St", "Austin", true);
            Gym riverside = ensureGym(gymRepository, "Riverside Strength Club", "222 Lake Ave", "Austin", true);
            Gym metro = ensureGym(gymRepository, "MetroFit Central", "88 Market Blvd", "Dallas", true);

            seedCodes(visitCodeRepository, downtown, List.of(
                    "AUS-DT-1001", "AUS-DT-1002", "AUS-DT-1003", "AUS-DT-1004", "AUS-DT-1005"
            ));
            seedCodes(visitCodeRepository, riverside, List.of(
                    "AUS-RV-2001", "AUS-RV-2002", "AUS-RV-2003", "AUS-RV-2004", "AUS-RV-2005"
            ));
            seedCodes(visitCodeRepository, metro, List.of(
                    "DAL-MT-3001", "DAL-MT-3002", "DAL-MT-3003", "DAL-MT-3004", "DAL-MT-3005"
            ));

            log.info("Seed complete: gyms={}, visitCodes={}", gymRepository.count(), visitCodeRepository.count());
        };
    }

    private Gym ensureGym(GymRepository gymRepository, String name, String address, String city, boolean active) {
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
                    return changed ? gymRepository.save(existing) : existing;
                })
                .orElseGet(() -> {
                    Gym gym = new Gym();
                    gym.setName(name);
                    gym.setAddress(address);
                    gym.setCity(city);
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
}
