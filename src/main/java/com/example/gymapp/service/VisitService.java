package com.example.gymapp.service;

import com.example.gymapp.dto.ScanCodeResponse;
import com.example.gymapp.dto.VisitResponse;
import com.example.gymapp.entity.Booking;
import com.example.gymapp.entity.BookingStatus;
import com.example.gymapp.entity.Gym;
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
    private final RewardService rewardService;

    public VisitService(
            VisitCodeRepository visitCodeRepository,
            BookingRepository bookingRepository,
            VisitRepository visitRepository,
            AuthenticatedUserUtil authenticatedUserUtil,
            StreakService streakService,
            BookingService bookingService,
            RewardService rewardService
    ) {
        this.visitCodeRepository = visitCodeRepository;
        this.bookingRepository = bookingRepository;
        this.visitRepository = visitRepository;
        this.authenticatedUserUtil = authenticatedUserUtil;
        this.streakService = streakService;
        this.bookingService = bookingService;
        this.rewardService = rewardService;
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

        Gym gym = visitCode.getGym();
        if (!gym.isActive()) {
            throw new BadRequestException("Gym is not active");
        }

        validateGymVisitWindow(gym);
        validateMaxDailyVisits(gym);

        LocalDate today = LocalDate.now();

        Booking booking = bookingRepository.findFirstByUserAndGymAndBookingDateAndStatusOrderByCreatedAtDesc(
                        user,
                        gym,
                        today,
                        BookingStatus.CREATED
                )
                .orElseThrow(() -> new BadRequestException("No created booking found for this gym and date"));

        if (!booking.getGym().getId().equals(gym.getId())) {
            throw new BadRequestException("Booking gym does not match QR code gym");
        }

        visitCode.setUsed(true);
        visitCode.setUsedByUser(user);
        visitCode.setUsedAt(LocalDateTime.now());
        visitCodeRepository.save(visitCode);

        booking.setStatus(BookingStatus.VISITED);
        bookingRepository.save(booking);

        Visit visit = new Visit();
        visit.setUser(user);
        visit.setGym(gym);
        visit.setBooking(booking);
        visit.setVisitedAt(LocalDateTime.now());
        Visit savedVisit = visitRepository.save(visit);

        UserStats stats = streakService.registerVisit(user, today);
        rewardService.processLoyaltyReward(user, gym);

        ScanCodeResponse response = new ScanCodeResponse();
        response.setVisitId(savedVisit.getId());
        response.setBookingId(booking.getId());
        response.setGymId(gym.getId());
        response.setGymName(gym.getName());
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

    private void validateMaxDailyVisits(Gym gym) {
        Integer maxDailyVisits = gym.getMaxDailyVisits();
        if (maxDailyVisits == null || maxDailyVisits <= 0) {
            return;
        }

        LocalDate today = LocalDate.now();
        long visitsToday = visitRepository.countByGymAndVisitedAtBetween(
                gym,
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay().minusNanos(1)
        );

        if (visitsToday >= maxDailyVisits) {
            throw new ConflictException("Gym has reached today's visit capacity");
        }
    }

    private void validateGymVisitWindow(Gym gym) {
        if (gym.getActiveFromTime() == null || gym.getActiveToTime() == null) {
            return;
        }

        var now = LocalDateTime.now().toLocalTime();
        var from = gym.getActiveFromTime();
        var to = gym.getActiveToTime();

        boolean withinWindow;
        if (from.isBefore(to)) {
            withinWindow = !now.isBefore(from) && !now.isAfter(to);
        } else {
            withinWindow = !now.isBefore(from) || !now.isAfter(to);
        }

        if (!withinWindow) {
            throw new BadRequestException("Gym is currently outside active visit hours");
        }
    }
}
