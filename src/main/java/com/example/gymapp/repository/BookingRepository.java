package com.example.gymapp.repository;

import com.example.gymapp.entity.Booking;
import com.example.gymapp.entity.BookingStatus;
import com.example.gymapp.entity.Gym;
import com.example.gymapp.entity.User;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    boolean existsByUserAndGymAndBookingDateAndStatusIn(User user, Gym gym, LocalDate bookingDate, Collection<BookingStatus> statuses);

    boolean existsByUserAndGymAndBookingDateAndStatusInAndIdNot(
            User user,
            Gym gym,
            LocalDate bookingDate,
            Collection<BookingStatus> statuses,
            Long id
    );

    List<Booking> findByUserOrderByBookingDateDescCreatedAtDesc(User user);

    List<Booking> findByUserAndStatusAndBookingDateBefore(User user, BookingStatus status, LocalDate bookingDate);

    Optional<Booking> findFirstByUserAndGymAndBookingDateAndStatusOrderByCreatedAtDesc(
            User user,
            Gym gym,
            LocalDate bookingDate,
            BookingStatus status
    );

    Optional<Booking> findByIdAndUser(Long id, User user);
}
