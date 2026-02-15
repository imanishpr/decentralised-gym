package com.example.gymapp.repository;

import com.example.gymapp.entity.User;
import com.example.gymapp.entity.UserStats;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStatsRepository extends JpaRepository<UserStats, Long> {

    Optional<UserStats> findByUser(User user);
}
