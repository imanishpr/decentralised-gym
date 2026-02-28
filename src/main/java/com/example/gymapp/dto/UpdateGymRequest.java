package com.example.gymapp.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalTime;
import org.hibernate.validator.constraints.URL;

public class UpdateGymRequest {

    @NotNull(message = "isActive is required")
    private Boolean active;

    @DecimalMin(value = "-90.0", message = "latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "latitude must be <= 90")
    private Double latitude;

    @DecimalMin(value = "-180.0", message = "longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "longitude must be <= 180")
    private Double longitude;

    @Size(max = 1000, message = "googleMapUrl cannot exceed 1000 characters")
    @URL(message = "googleMapUrl must be a valid URL")
    private String googleMapUrl;

    @Size(max = 1000, message = "imageUrl cannot exceed 1000 characters")
    @URL(message = "imageUrl must be a valid URL")
    private String imageUrl;

    @DecimalMin(value = "0.0", message = "pricePerHourInr must be >= 0")
    private Double pricePerHourInr;

    @NotNull(message = "maxDailyVisits is required")
    @Min(value = 1, message = "maxDailyVisits must be at least 1")
    @Max(value = 100000, message = "maxDailyVisits is too large")
    private Integer maxDailyVisits;

    @NotNull(message = "activeFromTime is required")
    private LocalTime activeFromTime;

    @NotNull(message = "activeToTime is required")
    private LocalTime activeToTime;

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getGoogleMapUrl() {
        return googleMapUrl;
    }

    public void setGoogleMapUrl(String googleMapUrl) {
        this.googleMapUrl = googleMapUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Double getPricePerHourInr() {
        return pricePerHourInr;
    }

    public void setPricePerHourInr(Double pricePerHourInr) {
        this.pricePerHourInr = pricePerHourInr;
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
