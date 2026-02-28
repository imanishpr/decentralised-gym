package com.example.gymapp.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalTime;

public class CreateGymRequest {

    @NotBlank(message = "name is required")
    @Size(max = 255, message = "name cannot exceed 255 characters")
    private String name;

    @NotBlank(message = "address is required")
    @Size(max = 255, message = "address cannot exceed 255 characters")
    private String address;

    @NotBlank(message = "city is required")
    @Size(max = 255, message = "city cannot exceed 255 characters")
    private String city;

    @NotNull(message = "maxDailyVisits is required")
    @Min(value = 1, message = "maxDailyVisits must be at least 1")
    @Max(value = 100000, message = "maxDailyVisits is too large")
    private Integer maxDailyVisits;

    @NotNull(message = "activeFromTime is required")
    private LocalTime activeFromTime;

    @NotNull(message = "activeToTime is required")
    private LocalTime activeToTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getMaxDailyVisits() {
        return maxDailyVisits;
    }

    public void setMaxDailyVisits(Integer maxDailyVisits) {
        this.maxDailyVisits = maxDailyVisits;
    }

    public LocalTime getActiveFromTime() {
        return activeFromTime;
    }

    public void setActiveFromTime(LocalTime activeFromTime) {
        this.activeFromTime = activeFromTime;
    }

    public LocalTime getActiveToTime() {
        return activeToTime;
    }

    public void setActiveToTime(LocalTime activeToTime) {
        this.activeToTime = activeToTime;
    }
}
