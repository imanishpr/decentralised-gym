package com.example.gymapp.dto;

import java.util.List;

public class GymAnalyticsSummaryResponse {

    private long totalVisitsToday;
    private long visitsThisMonth;
    private long uniqueUsersThisMonth;
    private long qrCodesUsed;
    private long qrCodesRemaining;
    private List<PeakHourResponse> peakVisitHours;

    public long getTotalVisitsToday() {
        return totalVisitsToday;
    }

    public void setTotalVisitsToday(long totalVisitsToday) {
        this.totalVisitsToday = totalVisitsToday;
    }

    public long getVisitsThisMonth() {
        return visitsThisMonth;
    }

    public void setVisitsThisMonth(long visitsThisMonth) {
        this.visitsThisMonth = visitsThisMonth;
    }

    public long getUniqueUsersThisMonth() {
        return uniqueUsersThisMonth;
    }

    public void setUniqueUsersThisMonth(long uniqueUsersThisMonth) {
        this.uniqueUsersThisMonth = uniqueUsersThisMonth;
    }

    public long getQrCodesUsed() {
        return qrCodesUsed;
    }

    public void setQrCodesUsed(long qrCodesUsed) {
        this.qrCodesUsed = qrCodesUsed;
    }

    public long getQrCodesRemaining() {
        return qrCodesRemaining;
    }

    public void setQrCodesRemaining(long qrCodesRemaining) {
        this.qrCodesRemaining = qrCodesRemaining;
    }

    public List<PeakHourResponse> getPeakVisitHours() {
        return peakVisitHours;
    }

    public void setPeakVisitHours(List<PeakHourResponse> peakVisitHours) {
        this.peakVisitHours = peakVisitHours;
    }
}
