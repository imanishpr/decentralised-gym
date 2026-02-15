package com.example.gymapp.repository;

import com.example.gymapp.entity.VisitCode;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitCodeRepository extends JpaRepository<VisitCode, Long> {

    Optional<VisitCode> findByCode(String code);

    boolean existsByCode(String code);
}
