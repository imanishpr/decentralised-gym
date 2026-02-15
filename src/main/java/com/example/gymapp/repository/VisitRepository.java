package com.example.gymapp.repository;

import com.example.gymapp.entity.User;
import com.example.gymapp.entity.Visit;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitRepository extends JpaRepository<Visit, Long> {

    List<Visit> findByUserOrderByVisitedAtDesc(User user);
}
