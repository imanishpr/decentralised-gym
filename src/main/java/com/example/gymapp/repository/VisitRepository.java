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

    interface HourCountProjection {
        Integer getHour();

        Long getTotal();
    }
}
