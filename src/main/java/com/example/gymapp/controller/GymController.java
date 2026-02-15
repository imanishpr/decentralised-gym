package com.example.gymapp.controller;

import com.example.gymapp.dto.GymResponse;
import com.example.gymapp.service.GymService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/gyms")
public class GymController {

    private final GymService gymService;

    public GymController(GymService gymService) {
        this.gymService = gymService;
    }

    @GetMapping("/active")
    public List<GymResponse> getActiveGyms() {
        return gymService.getActiveGyms();
    }
}
