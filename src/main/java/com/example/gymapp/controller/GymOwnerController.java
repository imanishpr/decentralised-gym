package com.example.gymapp.controller;

import com.example.gymapp.dto.CreateGymRequest;
import com.example.gymapp.dto.GymAnalyticsSummaryResponse;
import com.example.gymapp.dto.GymOwnerGymResponse;
import com.example.gymapp.service.GymOwnerService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/gym-owner")
public class GymOwnerController {

    private final GymOwnerService gymOwnerService;

    public GymOwnerController(GymOwnerService gymOwnerService) {
        this.gymOwnerService = gymOwnerService;
    }

    @PostMapping("/gym/create")
    @PreAuthorize("hasAnyRole('USER','GYM_OWNER','ADMIN')")
    public GymOwnerGymResponse createGym(@Valid @RequestBody CreateGymRequest request) {
        return gymOwnerService.createGym(request);
    }

    @GetMapping("/gym/my")
    @PreAuthorize("hasAnyRole('GYM_OWNER','ADMIN')")
    public GymOwnerGymResponse getMyGym() {
        return gymOwnerService.getMyGym();
    }

    @GetMapping("/analytics/summary")
    @PreAuthorize("hasAnyRole('GYM_OWNER','ADMIN')")
    public GymAnalyticsSummaryResponse getAnalyticsSummary() {
        return gymOwnerService.getAnalyticsSummary();
    }
}
