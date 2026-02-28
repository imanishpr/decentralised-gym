package com.example.gymapp.controller;

import com.example.gymapp.dto.RedeemRewardRequest;
import com.example.gymapp.dto.RewardResponse;
import com.example.gymapp.service.RewardService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/rewards")
@PreAuthorize("hasAnyRole('USER','GYM_OWNER','ADMIN')")
public class RewardController {

    private final RewardService rewardService;

    public RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    @GetMapping("/my-rewards")
    public List<RewardResponse> getMyRewards() {
        return rewardService.getMyRewards();
    }

    @PostMapping("/redeem")
    public RewardResponse redeem(@Valid @RequestBody RedeemRewardRequest request) {
        return rewardService.redeemReward(request);
    }
}
