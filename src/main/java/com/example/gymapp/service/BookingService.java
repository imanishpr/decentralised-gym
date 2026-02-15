package com.example.gymapp.service;

import com.example.gymapp.dto.BookingResponse;
import com.example.gymapp.dto.CreateBookingRequest;
import com.example.gymapp.entity.Booking;
import com.example.gymapp.entity.BookingStatus;
import com.example.gymapp.entity.Gym;
import com.example.gymapp.entity.User;
import com.example.gymapp.exception.BadRequestException;
import com.example.gymapp.exception.ConflictException;
import com.example.gymapp.exception.ResourceNotFoundException;
import com.example.gymapp.repository.BookingRepository;
import com.example.gymapp.repository.GymRepository;
import com.example.gymapp.util.AuthenticatedUserUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final GymRepository gymRepository;
    private final AuthenticatedUserUtil authenticatedUserUtil;

    public BookingService(
            BookingRepository bookingRepository,
            GymRepository gymRepository,
            AuthenticatedUserUtil authenticatedUserUtil
    ) {
        this.bookingRepository = bookingRepository;
        this.gymRepository = gymRepository;
        this.authenticatedUserUtil = authenticatedUserUtil;
    }

    @Transactional
    public BookingResponse createBooking(CreateBookingRequest request) {
        User user = authenticatedUserUtil.getCurrentUser();
        expirePastBookings(user);

        Gym gym = gymRepository.findById(request.getGymId())
                .orElseThrow(() -> new ResourceNotFoundException("Gym not found with id: " + request.getGymId()));

        if (!gym.isActive()) {
            throw new BadRequestException("Booking is allowed only for active gyms");
        }

        boolean alreadyBooked = bookingRepository.existsByUserAndGymAndBookingDateAndStatusIn(
                user,
                gym,
                request.getBookingDate(),
                Set.of(BookingStatus.CREATED, BookingStatus.VISITED)
        );

        if (alreadyBooked) {
            throw new ConflictException("Booking already exists for this gym and date");
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setGym(gym);
        booking.setBookingDate(request.getBookingDate());
        booking.setNote(request.getNote());
        booking.setStatus(BookingStatus.CREATED);
        booking.setCreatedAt(LocalDateTime.now());

        return toResponse(bookingRepository.save(booking));
    }

    @Transactional
    public List<BookingResponse> getMyBookings() {
        User user = authenticatedUserUtil.getCurrentUser();
        expirePastBookings(user);

        return bookingRepository.findByUserOrderByBookingDateDescCreatedAtDesc(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void expirePastBookings(User user) {
        List<Booking> bookingsToExpire = bookingRepository.findByUserAndStatusAndBookingDateBefore(
                user,
                BookingStatus.CREATED,
                LocalDate.now()
        );

        for (Booking booking : bookingsToExpire) {
            booking.setStatus(BookingStatus.EXPIRED);
        }

        if (!bookingsToExpire.isEmpty()) {
            bookingRepository.saveAll(bookingsToExpire);
        }
    }

    private BookingResponse toResponse(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setId(booking.getId());
        response.setGymId(booking.getGym().getId());
        response.setGymName(booking.getGym().getName());
        response.setBookingDate(booking.getBookingDate());
        response.setNote(booking.getNote());
        response.setStatus(booking.getStatus());
        response.setCreatedAt(booking.getCreatedAt());
        return response;
    }
}
