package com.example.gymapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalTime;

@Entity
@Table(name = "gyms")
public class Gym {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String city;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Column(length = 1000)
    private String googleMapUrl;

    @Column(length = 1000)
    private String imageUrl;

    @Column
    private Double pricePerHourInr;

    @Column(nullable = false)
    private boolean isActive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @Column
    private Integer maxDailyVisits;

    @Column
    private LocalTime activeFromTime;

    @Column
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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
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
