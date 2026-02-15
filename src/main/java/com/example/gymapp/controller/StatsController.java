package com.example.gymapp.controller;

import com.example.gymapp.dto.StreakResponse;
import com.example.gymapp.service.StatsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stats")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/my-streak")
    public StreakResponse getMyStreak() {
        return statsService.getMyStreak();
    }
}
