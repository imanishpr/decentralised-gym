package com.example.gymapp.controller;

import com.example.gymapp.dto.CreateGymRequest;
import com.example.gymapp.dto.CurrentGymUserResponse;
import com.example.gymapp.dto.GymAnalyticsSummaryResponse;
import com.example.gymapp.dto.GymOwnerGymResponse;
import com.example.gymapp.dto.UpdateGymRequest;
import com.example.gymapp.service.GymOwnerService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

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

    @GetMapping("/gyms")
    @PreAuthorize("hasAnyRole('GYM_OWNER','ADMIN')")
    public List<GymOwnerGymResponse> getManagedGyms() {
        return gymOwnerService.getManagedGyms();
    }

    @PutMapping("/gym/{gymId}")
    @PreAuthorize("hasAnyRole('GYM_OWNER','ADMIN')")
    public GymOwnerGymResponse updateGym(
            @PathVariable Long gymId,
            @Valid @RequestBody UpdateGymRequest request
    ) {
        return gymOwnerService.updateGym(gymId, request);
    }

    @GetMapping("/gym/{gymId}/current-users")
    @PreAuthorize("hasAnyRole('GYM_OWNER','ADMIN')")
    public List<CurrentGymUserResponse> getCurrentUsersInsideGym(@PathVariable Long gymId) {
        return gymOwnerService.getCurrentUsersInsideGym(gymId);
    }

    @GetMapping("/analytics/summary")
    @PreAuthorize("hasAnyRole('GYM_OWNER','ADMIN')")
    public GymAnalyticsSummaryResponse getAnalyticsSummary() {
        return gymOwnerService.getAnalyticsSummary();
    }
}
