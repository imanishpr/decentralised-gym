package com.example.gymapp.dto;

import com.example.gymapp.entity.RewardType;
import java.time.LocalDateTime;

public class RewardResponse {

    private Long id;
    private Long gymId;
    private String gymName;
    private RewardType type;
    private boolean redeemed;
    private LocalDateTime earnedAt;
    private LocalDateTime redeemedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGymId() {
        return gymId;
    }

    public void setGymId(Long gymId) {
        this.gymId = gymId;
    }

    public String getGymName() {
        return gymName;
    }

    public void setGymName(String gymName) {
        this.gymName = gymName;
    }

    public RewardType getType() {
        return type;
    }

    public void setType(RewardType type) {
        this.type = type;
    }

    public boolean isRedeemed() {
        return redeemed;
    }

    public void setRedeemed(boolean redeemed) {
        this.redeemed = redeemed;
    }

    public LocalDateTime getEarnedAt() {
        return earnedAt;
    }

    public void setEarnedAt(LocalDateTime earnedAt) {
        this.earnedAt = earnedAt;
    }

    public LocalDateTime getRedeemedAt() {
        return redeemedAt;
    }

    public void setRedeemedAt(LocalDateTime redeemedAt) {
        this.redeemedAt = redeemedAt;
    }
}
