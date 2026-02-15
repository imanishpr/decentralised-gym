package com.example.gymapp.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class CreateBookingRequest {

    @NotNull(message = "gymId is required")
    private Long gymId;

    @NotNull(message = "bookingDate is required")
    @FutureOrPresent(message = "bookingDate must be today or a future date")
    private LocalDate bookingDate;

    @Size(max = 500, message = "note cannot exceed 500 characters")
    private String note;

    public Long getGymId() {
        return gymId;
    }

    public void setGymId(Long gymId) {
        this.gymId = gymId;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
