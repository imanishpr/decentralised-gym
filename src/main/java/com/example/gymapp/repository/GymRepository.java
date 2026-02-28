package com.example.gymapp.repository;

import com.example.gymapp.entity.Gym;
import com.example.gymapp.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GymRepository extends JpaRepository<Gym, Long> {

    List<Gym> findByIsActiveTrueOrderByNameAsc();

    boolean existsByNameAndCity(String name, String city);

    Optional<Gym> findByNameAndCity(String name, String city);

    Optional<Gym> findByOwner(User owner);
}
