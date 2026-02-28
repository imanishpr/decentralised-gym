package com.example.gymapp.repository;

import com.example.gymapp.entity.Gym;
import com.example.gymapp.entity.QRBatch;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QRBatchRepository extends JpaRepository<QRBatch, Long> {

    List<QRBatch> findByGymOrderByCreatedAtDesc(Gym gym);
}
