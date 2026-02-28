package com.example.gymapp.service;

import com.example.gymapp.dto.BookingResponse;
import com.example.gymapp.dto.CreateBookingRequest;
import com.example.gymapp.dto.UpdateBookingRequest;
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
import java.time.LocalTime;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final GymRepository gymRepository;
    private final AuthenticatedUserUtil authenticatedUserUtil;
    private final int defaultDurationHours;
    private final Set<Integer> allowedDurationHours;

    public BookingService(
            BookingRepository bookingRepository,
            GymRepository gymRepository,
            AuthenticatedUserUtil authenticatedUserUtil,
            @Value("${app.booking.default-duration-hours:1}") int defaultDurationHours,
            @Value("${app.booking.allowed-duration-hours:1,2}") String allowedDurationHoursCsv
    ) {
        this.bookingRepository = bookingRepository;
        this.gymRepository = gymRepository;
        this.authenticatedUserUtil = authenticatedUserUtil;
        this.defaultDurationHours = defaultDurationHours;
        this.allowedDurationHours = parseAllowedDurations(allowedDurationHoursCsv);
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

        int durationHours = request.getDurationHours() == null ? defaultDurationHours : request.getDurationHours();
        if (!allowedDurationHours.contains(durationHours)) {
            throw new BadRequestException("durationHours must be one of: " + allowedDurationHours);
        }

        LocalTime startTime = request.getStartTime();
        LocalTime endTime = startTime.plusHours(durationHours);
        if (endTime.isBefore(startTime)) {
            throw new BadRequestException("Booking end time cannot cross midnight");
        }
        validateGymHours(gym, startTime, endTime);
        validateSameDayWindow(request.getBookingDate(), startTime, durationHours, gym);

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
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setDurationHours(durationHours);
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
    public BookingResponse updateBooking(Long bookingId, UpdateBookingRequest request) {
        User user = authenticatedUserUtil.getCurrentUser();
        expirePastBookings(user);

        Booking booking = bookingRepository.findByIdAndUser(bookingId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if (booking.getStatus() != BookingStatus.CREATED) {
            throw new BadRequestException("Only CREATED bookings can be updated");
        }

        int durationHours = request.getDurationHours() == null ? defaultDurationHours : request.getDurationHours();
        if (!allowedDurationHours.contains(durationHours)) {
            throw new BadRequestException("durationHours must be one of: " + allowedDurationHours);
        }

        LocalTime startTime = request.getStartTime();
        LocalTime endTime = startTime.plusHours(durationHours);
        if (endTime.isBefore(startTime)) {
            throw new BadRequestException("Booking end time cannot cross midnight");
        }
        validateGymHours(booking.getGym(), startTime, endTime);
        validateSameDayWindow(request.getBookingDate(), startTime, durationHours, booking.getGym());

        boolean alreadyBooked = bookingRepository.existsByUserAndGymAndBookingDateAndStatusInAndIdNot(
                user,
                booking.getGym(),
                request.getBookingDate(),
                Set.of(BookingStatus.CREATED, BookingStatus.VISITED),
                bookingId
        );
        if (alreadyBooked) {
            throw new ConflictException("Booking already exists for this gym and date");
        }

        booking.setBookingDate(request.getBookingDate());
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setDurationHours(durationHours);
        booking.setNote(request.getNote());

        return toResponse(bookingRepository.save(booking));
    }

    @Transactional
    public void deleteBooking(Long bookingId) {
        User user = authenticatedUserUtil.getCurrentUser();
        expirePastBookings(user);

        Booking booking = bookingRepository.findByIdAndUser(bookingId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if (booking.getStatus() != BookingStatus.CREATED) {
            throw new BadRequestException("Only CREATED bookings can be deleted");
        }

        bookingRepository.delete(booking);
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
        response.setStartTime(booking.getStartTime());
        response.setEndTime(booking.getEndTime());
        response.setDurationHours(booking.getDurationHours());
        response.setNote(booking.getNote());
        response.setStatus(booking.getStatus());
        response.setCreatedAt(booking.getCreatedAt());
        return response;
    }

    private Set<Integer> parseAllowedDurations(String csv) {
        Set<Integer> parsed = Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .collect(LinkedHashSet::new, Set::add, Set::addAll);

        if (parsed.isEmpty()) {
            return Set.of(1, 2);
        }
        return parsed;
    }

    private void validateGymHours(Gym gym, LocalTime startTime, LocalTime endTime) {
        LocalTime activeFrom = gym.getActiveFromTime();
        LocalTime activeTo = gym.getActiveToTime();

        if (activeFrom == null || activeTo == null) {
            return;
        }

        if (startTime.isBefore(activeFrom) || endTime.isAfter(activeTo)) {
            throw new BadRequestException(
                    "Booking time must be within gym active hours: " + activeFrom + " to " + activeTo
            );
        }
    }

    private void validateSameDayWindow(LocalDate bookingDate, LocalTime startTime, int durationHours, Gym gym) {
        if (!LocalDate.now().equals(bookingDate)) {
            return;
        }

        LocalTime now = LocalTime.now().withSecond(0).withNano(0);
        if (startTime.isBefore(now)) {
            throw new BadRequestException("For today, startTime must be current or future time");
        }

        LocalTime activeTo = gym.getActiveToTime();
        if (activeTo == null) {
            return;
        }

        LocalTime nowPlusDuration = now.plusHours(durationHours);
        if (nowPlusDuration.isAfter(activeTo)) {
            throw new BadRequestException("Current time plus duration exceeds gym closing time");
        }
    }
}
