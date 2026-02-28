package com.example.gymapp.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateQrBatchRequest {

    private Long gymId;

    @NotBlank(message = "batchName is required")
    @Size(max = 255, message = "batchName cannot exceed 255 characters")
    private String batchName;

    @NotNull(message = "totalCodes is required")
    @Min(value = 1, message = "totalCodes must be at least 1")
    @Max(value = 10000, message = "totalCodes must be at most 10000")
    private Integer totalCodes;

    public String getBatchName() {
        return batchName;
    }

    public Long getGymId() {
        return gymId;
    }

    public void setGymId(Long gymId) {
        this.gymId = gymId;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    public Integer getTotalCodes() {
        return totalCodes;
    }

    public void setTotalCodes(Integer totalCodes) {
        this.totalCodes = totalCodes;
    }
}
