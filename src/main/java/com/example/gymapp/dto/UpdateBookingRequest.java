package com.example.gymapp.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;

public class UpdateBookingRequest {

    @NotNull(message = "bookingDate is required")
    @FutureOrPresent(message = "bookingDate must be today or a future date")
    private LocalDate bookingDate;

    @NotNull(message = "startTime is required")
    private LocalTime startTime;

    @Min(value = 1, message = "durationHours must be at least 1")
    @Max(value = 2, message = "durationHours cannot exceed 2")
    private Integer durationHours;

    @Size(max = 500, message = "note cannot exceed 500 characters")
    private String note;

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public Integer getDurationHours() {
        return durationHours;
    }

    public void setDurationHours(Integer durationHours) {
        this.durationHours = durationHours;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
