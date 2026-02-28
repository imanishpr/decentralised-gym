package com.example.gymapp.dto;

import java.time.LocalDateTime;
import java.util.List;

public class QrBatchResponse {

    private Long id;
    private Long gymId;
    private String gymName;
    private String batchName;
    private int totalCodes;
    private LocalDateTime createdAt;
    private List<String> codes;

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

    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    public int getTotalCodes() {
        return totalCodes;
    }

    public void setTotalCodes(int totalCodes) {
        this.totalCodes = totalCodes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getCodes() {
        return codes;
    }

    public void setCodes(List<String> codes) {
        this.codes = codes;
    }
}
