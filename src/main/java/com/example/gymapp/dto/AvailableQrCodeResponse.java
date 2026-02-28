package com.example.gymapp.dto;

import java.time.LocalDateTime;

public class AvailableQrCodeResponse {

    private Long id;
    private String code;
    private Long gymId;
    private String gymName;
    private Long batchId;
    private String batchName;
    private LocalDateTime issuedToGymAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    public LocalDateTime getIssuedToGymAt() {
        return issuedToGymAt;
    }

    public void setIssuedToGymAt(LocalDateTime issuedToGymAt) {
        this.issuedToGymAt = issuedToGymAt;
    }
}
