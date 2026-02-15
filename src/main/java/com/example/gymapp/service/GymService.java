package com.example.gymapp.service;

import com.example.gymapp.dto.GymResponse;
import com.example.gymapp.entity.Gym;
import com.example.gymapp.repository.GymRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GymService {

    private final GymRepository gymRepository;

    public GymService(GymRepository gymRepository) {
        this.gymRepository = gymRepository;
    }

    public List<GymResponse> getActiveGyms() {
        return gymRepository.findByIsActiveTrueOrderByNameAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private GymResponse toResponse(Gym gym) {
        GymResponse response = new GymResponse();
        response.setId(gym.getId());
        response.setName(gym.getName());
        response.setAddress(gym.getAddress());
        response.setCity(gym.getCity());
        response.setActive(gym.isActive());
        return response;
    }
}
