package com.example.gymapp.repository;

import com.example.gymapp.entity.Gym;
import com.example.gymapp.entity.QRBatch;
import com.example.gymapp.entity.VisitCode;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitCodeRepository extends JpaRepository<VisitCode, Long> {

    Optional<VisitCode> findByCode(String code);

    boolean existsByCode(String code);

    long countByGymAndIsUsed(Gym gym, boolean isUsed);

    List<VisitCode> findTop200ByGymAndIsUsedOrderByIdDesc(Gym gym, boolean isUsed);

    List<VisitCode> findByBatchOrderByIdAsc(QRBatch batch);
}
