package com.example.gymapp.dto;

import java.time.LocalTime;

public class GymOwnerGymResponse {

    private Long id;
    private String name;
    private String address;
    private String city;
    private boolean active;
    private Integer maxDailyVisits;
    private LocalTime activeFromTime;
    private LocalTime activeToTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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
