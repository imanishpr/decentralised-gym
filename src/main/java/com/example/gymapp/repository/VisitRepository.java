package com.example.gymapp.repository;

import com.example.gymapp.entity.Gym;
import com.example.gymapp.entity.User;
import com.example.gymapp.entity.Visit;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VisitRepository extends JpaRepository<Visit, Long> {

    List<Visit> findByUserOrderByVisitedAtDesc(User user);

    long countByGymAndVisitedAtBetween(Gym gym, LocalDateTime start, LocalDateTime end);

    long countByUserAndGym(User user, Gym gym);

    @Query("""
            select count(distinct v.user.id)
            from Visit v
            where v.gym = :gym and v.visitedAt between :start and :end
            """)
    long countDistinctUsersByGymAndPeriod(@Param("gym") Gym gym, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("""
            select function('hour', v.visitedAt) as hour, count(v.id) as total
            from Visit v
            where v.gym = :gym and v.visitedAt between :start and :end
            group by function('hour', v.visitedAt)
            order by total desc
            """)
    List<HourCountProjection> findPeakHours(@Param("gym") Gym gym, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query(
            value = """
                    select
                        u.id as userId,
                        u.name as userName,
                        u.email as userEmail,
                        max(v.visited_at) as lastVisitedAt,
                        cast(b.start_time as char) as bookingStartTime,
                        cast(b.end_time as char) as bookingEndTime
                    from visits v
                    join users u on u.id = v.user_id
                    join bookings b on b.id = v.booking_id
                    where v.gym_id = :gymId
                      and b.booking_date = :today
                      and b.status = 'VISITED'
                      and :now between timestamp(b.booking_date, b.start_time)
                               and timestamp(b.booking_date, b.end_time)
                    group by u.id, u.name, u.email, b.start_time, b.end_time
                    order by lastVisitedAt desc
                    """,
            nativeQuery = true
    )
    List<CurrentGymUserProjection> findCurrentUsersInsideGym(
            @Param("gymId") Long gymId,
            @Param("today") java.time.LocalDate today,
            @Param("now") LocalDateTime now
    );

    interface HourCountProjection {
        Integer getHour();

        Long getTotal();
    }

    interface CurrentGymUserProjection {
        Long getUserId();

        String getUserName();

        String getUserEmail();

        LocalDateTime getLastVisitedAt();

        String getBookingStartTime();

        String getBookingEndTime();
    }
}
