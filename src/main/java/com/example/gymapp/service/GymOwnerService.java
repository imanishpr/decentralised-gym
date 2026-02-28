package com.example.gymapp.service;

import com.example.gymapp.dto.CreateGymRequest;
import com.example.gymapp.dto.GymAnalyticsSummaryResponse;
import com.example.gymapp.dto.GymOwnerGymResponse;
import com.example.gymapp.dto.PeakHourResponse;
import com.example.gymapp.entity.Gym;
import com.example.gymapp.entity.User;
import com.example.gymapp.entity.UserRole;
import com.example.gymapp.exception.BadRequestException;
import com.example.gymapp.exception.ConflictException;
import com.example.gymapp.exception.ResourceNotFoundException;
import com.example.gymapp.repository.GymRepository;
import com.example.gymapp.repository.UserRepository;
import com.example.gymapp.repository.VisitCodeRepository;
import com.example.gymapp.repository.VisitRepository;
import com.example.gymapp.util.AuthenticatedUserUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GymOwnerService {

    private final AuthenticatedUserUtil authenticatedUserUtil;
    private final GymRepository gymRepository;
    private final UserRepository userRepository;
    private final VisitRepository visitRepository;
    private final VisitCodeRepository visitCodeRepository;

    public GymOwnerService(
            AuthenticatedUserUtil authenticatedUserUtil,
            GymRepository gymRepository,
            UserRepository userRepository,
            VisitRepository visitRepository,
            VisitCodeRepository visitCodeRepository
    ) {
        this.authenticatedUserUtil = authenticatedUserUtil;
        this.gymRepository = gymRepository;
        this.userRepository = userRepository;
        this.visitRepository = visitRepository;
        this.visitCodeRepository = visitCodeRepository;
    }

    @Transactional
    public GymOwnerGymResponse createGym(CreateGymRequest request) {
        User user = authenticatedUserUtil.getCurrentUser();

        if (request.getActiveFromTime().equals(request.getActiveToTime())) {
            throw new BadRequestException("activeFromTime and activeToTime cannot be the same");
        }

        if (gymRepository.findByOwner(user).isPresent()) {
            throw new ConflictException("Gym owner already has a gym");
        }

        Gym gym = new Gym();
        gym.setName(request.getName().trim());
        gym.setAddress(request.getAddress().trim());
        gym.setCity(request.getCity().trim());
        gym.setOwner(user);
        gym.setMaxDailyVisits(request.getMaxDailyVisits());
        gym.setActiveFromTime(request.getActiveFromTime());
        gym.setActiveToTime(request.getActiveToTime());
        gym.setActive(true);

        Gym savedGym = gymRepository.save(gym);

        if (user.getRole() != UserRole.GYM_OWNER) {
            user.setRole(UserRole.GYM_OWNER);
            userRepository.save(user);
        }

        return toGymResponse(savedGym);
    }

    @Transactional(readOnly = true)
    public GymOwnerGymResponse getMyGym() {
        Gym gym = getOwnerGym();
        return toGymResponse(gym);
    }

    @Transactional(readOnly = true)
    public GymAnalyticsSummaryResponse getAnalyticsSummary() {
        Gym gym = getOwnerGym();

        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.plusDays(1).atStartOfDay().minusNanos(1);

        YearMonth month = YearMonth.now();
        LocalDateTime monthStart = month.atDay(1).atStartOfDay();
        LocalDateTime monthEnd = month.atEndOfMonth().atTime(23, 59, 59, 999_999_999);

        long totalVisitsToday = visitRepository.countByGymAndVisitedAtBetween(gym, todayStart, todayEnd);
        long visitsThisMonth = visitRepository.countByGymAndVisitedAtBetween(gym, monthStart, monthEnd);
        long uniqueUsersThisMonth = visitRepository.countDistinctUsersByGymAndPeriod(gym, monthStart, monthEnd);

        long qrCodesUsed = visitCodeRepository.countByGymAndIsUsed(gym, true);
        long qrCodesRemaining = visitCodeRepository.countByGymAndIsUsed(gym, false);

        List<PeakHourResponse> peakHours = visitRepository.findPeakHours(gym, monthStart, monthEnd)
                .stream()
                .limit(5)
                .map(row -> {
                    PeakHourResponse peakHour = new PeakHourResponse();
                    peakHour.setHour(row.getHour() == null ? 0 : row.getHour());
                    peakHour.setTotalVisits(row.getTotal() == null ? 0 : row.getTotal());
                    return peakHour;
                })
                .toList();

        GymAnalyticsSummaryResponse response = new GymAnalyticsSummaryResponse();
        response.setTotalVisitsToday(totalVisitsToday);
        response.setVisitsThisMonth(visitsThisMonth);
        response.setUniqueUsersThisMonth(uniqueUsersThisMonth);
        response.setQrCodesUsed(qrCodesUsed);
        response.setQrCodesRemaining(qrCodesRemaining);
        response.setPeakVisitHours(peakHours);
        return response;
    }

    @Transactional(readOnly = true)
    public Gym getOwnerGym() {
        User user = authenticatedUserUtil.getCurrentUser();
        return gymRepository.findByOwner(user)
                .orElseThrow(() -> new ResourceNotFoundException("No gym found for this owner"));
    }

    private GymOwnerGymResponse toGymResponse(Gym gym) {
        GymOwnerGymResponse response = new GymOwnerGymResponse();
        response.setId(gym.getId());
        response.setName(gym.getName());
        response.setAddress(gym.getAddress());
        response.setCity(gym.getCity());
        response.setActive(gym.isActive());
        response.setMaxDailyVisits(gym.getMaxDailyVisits());
        response.setActiveFromTime(gym.getActiveFromTime());
        response.setActiveToTime(gym.getActiveToTime());
        return response;
    }
}
