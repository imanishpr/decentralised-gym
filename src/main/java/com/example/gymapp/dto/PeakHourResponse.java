package com.example.gymapp.dto;

public class PeakHourResponse {

    private int hour;
    private long totalVisits;

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public long getTotalVisits() {
        return totalVisits;
    }

    public void setTotalVisits(long totalVisits) {
        this.totalVisits = totalVisits;
    }
}
