package com.example.gymapp.repository;

import com.example.gymapp.entity.Gym;
import com.example.gymapp.entity.Reward;
import com.example.gymapp.entity.RewardType;
import com.example.gymapp.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RewardRepository extends JpaRepository<Reward, Long> {

    long countByUserAndGym(User user, Gym gym);

    List<Reward> findByUserOrderByEarnedAtDesc(User user);

    Optional<Reward> findByIdAndUser(Long id, User user);

    Optional<Reward> findFirstByUserAndGymAndTypeAndIsRedeemedOrderByEarnedAtAsc(
            User user,
            Gym gym,
            RewardType type,
            boolean isRedeemed
    );
}
