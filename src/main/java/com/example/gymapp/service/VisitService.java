package com.example.gymapp.service;

import com.example.gymapp.dto.ScanCodeResponse;
import com.example.gymapp.dto.VisitResponse;
import com.example.gymapp.entity.Booking;
import com.example.gymapp.entity.BookingStatus;
import com.example.gymapp.entity.User;
import com.example.gymapp.entity.UserStats;
import com.example.gymapp.entity.Visit;
import com.example.gymapp.entity.VisitCode;
import com.example.gymapp.exception.BadRequestException;
import com.example.gymapp.exception.ConflictException;
import com.example.gymapp.exception.ResourceNotFoundException;
import com.example.gymapp.repository.BookingRepository;
import com.example.gymapp.repository.VisitCodeRepository;
import com.example.gymapp.repository.VisitRepository;
import com.example.gymapp.util.AuthenticatedUserUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VisitService {

    private final VisitCodeRepository visitCodeRepository;
    private final BookingRepository bookingRepository;
    private final VisitRepository visitRepository;
    private final AuthenticatedUserUtil authenticatedUserUtil;
    private final StreakService streakService;
    private final BookingService bookingService;

    public VisitService(
            VisitCodeRepository visitCodeRepository,
            BookingRepository bookingRepository,
            VisitRepository visitRepository,
            AuthenticatedUserUtil authenticatedUserUtil,
            StreakService streakService,
            BookingService bookingService
    ) {
        this.visitCodeRepository = visitCodeRepository;
        this.bookingRepository = bookingRepository;
        this.visitRepository = visitRepository;
        this.authenticatedUserUtil = authenticatedUserUtil;
        this.streakService = streakService;
        this.bookingService = bookingService;
    }

    @Transactional
    public ScanCodeResponse scanCode(String code) {
        User user = authenticatedUserUtil.getCurrentUser();
        bookingService.expirePastBookings(user);

        VisitCode visitCode = visitCodeRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Visit code not found"));

        if (visitCode.isUsed()) {
            throw new ConflictException("Visit code has already been used");
        }

        LocalDate today = LocalDate.now();

        Booking booking = bookingRepository.findFirstByUserAndGymAndBookingDateAndStatusOrderByCreatedAtDesc(
                        user,
                        visitCode.getGym(),
                        today,
                        BookingStatus.CREATED
                )
                .orElseThrow(() -> new BadRequestException("No created booking found for this gym and date"));

        visitCode.setUsed(true);
        visitCode.setUsedAt(LocalDateTime.now());
        visitCodeRepository.save(visitCode);

        booking.setStatus(BookingStatus.VISITED);
        bookingRepository.save(booking);

        Visit visit = new Visit();
        visit.setUser(user);
        visit.setGym(visitCode.getGym());
        visit.setBooking(booking);
        visit.setVisitedAt(LocalDateTime.now());
        Visit savedVisit = visitRepository.save(visit);

        UserStats stats = streakService.registerVisit(user, today);

        ScanCodeResponse response = new ScanCodeResponse();
        response.setVisitId(savedVisit.getId());
        response.setBookingId(booking.getId());
        response.setGymId(visitCode.getGym().getId());
        response.setGymName(visitCode.getGym().getName());
        response.setVisitedAt(savedVisit.getVisitedAt());
        response.setCurrentStreak(stats.getCurrentStreak());
        response.setLongestStreak(stats.getLongestStreak());
        response.setLastVisitDate(stats.getLastVisitDate());
        return response;
    }

    @Transactional(readOnly = true)
    public List<VisitResponse> getMyVisits() {
        User user = authenticatedUserUtil.getCurrentUser();

        return visitRepository.findByUserOrderByVisitedAtDesc(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private VisitResponse toResponse(Visit visit) {
        VisitResponse response = new VisitResponse();
        response.setId(visit.getId());
        response.setGymId(visit.getGym().getId());
        response.setGymName(visit.getGym().getName());
        response.setBookingId(visit.getBooking().getId());
        response.setVisitedAt(visit.getVisitedAt());
        return response;
    }
}
