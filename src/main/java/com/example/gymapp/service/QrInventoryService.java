package com.example.gymapp.service;

import com.example.gymapp.dto.AvailableQrCodeResponse;
import com.example.gymapp.dto.CreateQrBatchRequest;
import com.example.gymapp.dto.QrBatchResponse;
import com.example.gymapp.entity.Gym;
import com.example.gymapp.entity.QRBatch;
import com.example.gymapp.entity.VisitCode;
import com.example.gymapp.exception.ResourceNotFoundException;
import com.example.gymapp.repository.QRBatchRepository;
import com.example.gymapp.repository.VisitCodeRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QrInventoryService {

    private final GymOwnerService gymOwnerService;
    private final QRBatchRepository qrBatchRepository;
    private final VisitCodeRepository visitCodeRepository;

    public QrInventoryService(
            GymOwnerService gymOwnerService,
            QRBatchRepository qrBatchRepository,
            VisitCodeRepository visitCodeRepository
    ) {
        this.gymOwnerService = gymOwnerService;
        this.qrBatchRepository = qrBatchRepository;
        this.visitCodeRepository = visitCodeRepository;
    }

    @Transactional
    public QrBatchResponse createBatch(CreateQrBatchRequest request) {
        Gym gym = gymOwnerService.getOwnerGym();

        QRBatch batch = new QRBatch();
        batch.setGym(gym);
        batch.setBatchName(request.getBatchName().trim());
        batch.setTotalCodes(request.getTotalCodes());
        batch.setCreatedAt(LocalDateTime.now());
        QRBatch savedBatch = qrBatchRepository.save(batch);

        List<String> generatedCodes = new ArrayList<>();
        for (int i = 0; i < request.getTotalCodes(); i++) {
            String code = generateUniqueCode(gym);
            generatedCodes.add(code);

            VisitCode visitCode = new VisitCode();
            visitCode.setCode(code);
            visitCode.setGym(gym);
            visitCode.setBatch(savedBatch);
            visitCode.setIssuedToGymAt(LocalDateTime.now());
            visitCode.setUsed(false);
            visitCode.setUsedByUser(null);
            visitCode.setUsedAt(null);
            visitCodeRepository.save(visitCode);
        }

        return toBatchResponse(savedBatch, generatedCodes);
    }

    @Transactional(readOnly = true)
    public QrBatchResponse getBatch(Long batchId) {
        Gym gym = gymOwnerService.getOwnerGym();

        QRBatch batch = qrBatchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("QR batch not found"));

        if (!batch.getGym().getId().equals(gym.getId())) {
            throw new ResourceNotFoundException("QR batch not found for this gym");
        }

        List<String> codes = visitCodeRepository.findByBatchOrderByIdAsc(batch)
                .stream()
                .map(VisitCode::getCode)
                .toList();

        return toBatchResponse(batch, codes);
    }

    @Transactional(readOnly = true)
    public List<AvailableQrCodeResponse> getAvailableCodes() {
        Gym gym = gymOwnerService.getOwnerGym();

        return visitCodeRepository.findTop200ByGymAndIsUsedOrderByIdDesc(gym, false)
                .stream()
                .map(this::toAvailableResponse)
                .toList();
    }

    private String generateUniqueCode(Gym gym) {
        String gymPart = String.format(Locale.ROOT, "G%04d", gym.getId());
        String dayPart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd", Locale.ROOT));

        String code;
        do {
            String randomPart = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase(Locale.ROOT);
            code = gymPart + "-" + dayPart + "-" + randomPart;
        } while (visitCodeRepository.existsByCode(code));

        return code;
    }

    private QrBatchResponse toBatchResponse(QRBatch batch, List<String> codes) {
        QrBatchResponse response = new QrBatchResponse();
        response.setId(batch.getId());
        response.setGymId(batch.getGym().getId());
        response.setGymName(batch.getGym().getName());
        response.setBatchName(batch.getBatchName());
        response.setTotalCodes(batch.getTotalCodes());
        response.setCreatedAt(batch.getCreatedAt());
        response.setCodes(codes);
        return response;
    }

    private AvailableQrCodeResponse toAvailableResponse(VisitCode visitCode) {
        AvailableQrCodeResponse response = new AvailableQrCodeResponse();
        response.setId(visitCode.getId());
        response.setCode(visitCode.getCode());
        response.setGymId(visitCode.getGym().getId());
        response.setGymName(visitCode.getGym().getName());
        response.setIssuedToGymAt(visitCode.getIssuedToGymAt());
        if (visitCode.getBatch() != null) {
            response.setBatchId(visitCode.getBatch().getId());
            response.setBatchName(visitCode.getBatch().getBatchName());
        }
        return response;
    }
}
