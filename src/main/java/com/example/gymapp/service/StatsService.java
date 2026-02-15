package com.example.gymapp.service;

import com.example.gymapp.dto.StreakResponse;
import com.example.gymapp.entity.User;
import com.example.gymapp.entity.UserStats;
import com.example.gymapp.repository.UserStatsRepository;
import com.example.gymapp.util.AuthenticatedUserUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StatsService {

    private final UserStatsRepository userStatsRepository;
    private final AuthenticatedUserUtil authenticatedUserUtil;

    public StatsService(UserStatsRepository userStatsRepository, AuthenticatedUserUtil authenticatedUserUtil) {
        this.userStatsRepository = userStatsRepository;
        this.authenticatedUserUtil = authenticatedUserUtil;
    }

    @Transactional(readOnly = true)
    public StreakResponse getMyStreak() {
        User user = authenticatedUserUtil.getCurrentUser();

        UserStats stats = userStatsRepository.findByUser(user).orElseGet(() -> {
            UserStats emptyStats = new UserStats();
            emptyStats.setUser(user);
            emptyStats.setCurrentStreak(0);
            emptyStats.setLongestStreak(0);
            return emptyStats;
        });

        StreakResponse response = new StreakResponse();
        response.setCurrentStreak(stats.getCurrentStreak());
        response.setLongestStreak(stats.getLongestStreak());
        response.setLastVisitDate(stats.getLastVisitDate());
        return response;
    }
}
