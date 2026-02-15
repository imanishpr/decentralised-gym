package com.example.gymapp.service;

import com.example.gymapp.entity.User;
import com.example.gymapp.entity.UserStats;
import com.example.gymapp.repository.UserStatsRepository;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StreakService {

    private final UserStatsRepository userStatsRepository;

    public StreakService(UserStatsRepository userStatsRepository) {
        this.userStatsRepository = userStatsRepository;
    }

    @Transactional
    public UserStats registerVisit(User user, LocalDate visitDate) {
        UserStats stats = userStatsRepository.findByUser(user).orElseGet(() -> {
            UserStats newStats = new UserStats();
            newStats.setUser(user);
            newStats.setCurrentStreak(0);
            newStats.setLongestStreak(0);
            return newStats;
        });

        LocalDate lastVisitDate = stats.getLastVisitDate();

        if (lastVisitDate == null) {
            stats.setCurrentStreak(1);
        } else if (lastVisitDate.isEqual(visitDate)) {
            return stats;
        } else if (lastVisitDate.plusDays(1).isEqual(visitDate)) {
            stats.setCurrentStreak(stats.getCurrentStreak() + 1);
        } else {
            stats.setCurrentStreak(1);
        }

        if (stats.getCurrentStreak() > stats.getLongestStreak()) {
            stats.setLongestStreak(stats.getCurrentStreak());
        }

        stats.setLastVisitDate(visitDate);
        return userStatsRepository.save(stats);
    }
}
