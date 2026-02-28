package com.example.gymapp.service;

import com.example.gymapp.dto.RedeemRewardRequest;
import com.example.gymapp.dto.RewardResponse;
import com.example.gymapp.entity.Gym;
import com.example.gymapp.entity.Reward;
import com.example.gymapp.entity.RewardType;
import com.example.gymapp.entity.User;
import com.example.gymapp.exception.BadRequestException;
import com.example.gymapp.exception.ResourceNotFoundException;
import com.example.gymapp.repository.RewardRepository;
import com.example.gymapp.repository.VisitRepository;
import com.example.gymapp.util.AuthenticatedUserUtil;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RewardService {

    private final RewardRepository rewardRepository;
    private final VisitRepository visitRepository;
    private final AuthenticatedUserUtil authenticatedUserUtil;

    public RewardService(
            RewardRepository rewardRepository,
            VisitRepository visitRepository,
            AuthenticatedUserUtil authenticatedUserUtil
    ) {
        this.rewardRepository = rewardRepository;
        this.visitRepository = visitRepository;
        this.authenticatedUserUtil = authenticatedUserUtil;
    }

    @Transactional
    public void processLoyaltyReward(User user, Gym gym) {
        long totalVisits = visitRepository.countByUserAndGym(user, gym);
        if (totalVisits <= 0 || totalVisits % 10 != 0) {
            return;
        }

        long rewardCount = rewardRepository.countByUserAndGym(user, gym);
        if (rewardCount >= (totalVisits / 10)) {
            return;
        }

        Reward reward = new Reward();
        reward.setUser(user);
        reward.setGym(gym);
        reward.setType(RewardType.FREE_VISIT);
        reward.setRedeemed(false);
        reward.setEarnedAt(LocalDateTime.now());
        reward.setRedeemedAt(null);
        rewardRepository.save(reward);
    }

    @Transactional(readOnly = true)
    public List<RewardResponse> getMyRewards() {
        User user = authenticatedUserUtil.getCurrentUser();

        return rewardRepository.findByUserOrderByEarnedAtDesc(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public RewardResponse redeemReward(RedeemRewardRequest request) {
        User user = authenticatedUserUtil.getCurrentUser();

        Reward reward = rewardRepository.findByIdAndUser(request.getRewardId(), user)
                .orElseThrow(() -> new ResourceNotFoundException("Reward not found"));

        if (reward.isRedeemed()) {
            throw new BadRequestException("Reward is already redeemed");
        }

        reward.setRedeemed(true);
        reward.setRedeemedAt(LocalDateTime.now());
        Reward savedReward = rewardRepository.save(reward);
        return toResponse(savedReward);
    }

    private RewardResponse toResponse(Reward reward) {
        RewardResponse response = new RewardResponse();
        response.setId(reward.getId());
        response.setGymId(reward.getGym().getId());
        response.setGymName(reward.getGym().getName());
        response.setType(reward.getType());
        response.setRedeemed(reward.isRedeemed());
        response.setEarnedAt(reward.getEarnedAt());
        response.setRedeemedAt(reward.getRedeemedAt());
        return response;
    }
}
