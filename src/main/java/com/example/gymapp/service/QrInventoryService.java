package com.example.gymapp.service;

import com.example.gymapp.dto.AvailableQrCodeResponse;
import com.example.gymapp.dto.CreateQrBatchRequest;
import com.example.gymapp.dto.QrCodeImageResponse;
import com.example.gymapp.dto.QrBatchResponse;
import com.example.gymapp.entity.Gym;
import com.example.gymapp.entity.QRBatch;
import com.example.gymapp.entity.VisitCode;
import com.example.gymapp.exception.BadRequestException;
import com.example.gymapp.exception.ResourceNotFoundException;
import com.example.gymapp.repository.QRBatchRepository;
import com.example.gymapp.repository.VisitCodeRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
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
        Gym gym = request.getGymId() == null
                ? gymOwnerService.getOwnerGym()
                : gymOwnerService.getAccessibleGym(request.getGymId());

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

    @Transactional(readOnly = true)
    public List<AvailableQrCodeResponse> getAvailableCodesByGymId(Long gymId) {
        Gym gym = gymOwnerService.getAccessibleGym(gymId);
        return visitCodeRepository.findTop200ByGymAndIsUsedOrderByIdDesc(gym, false)
                .stream()
                .map(this::toAvailableResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<QrCodeImageResponse> getAvailableCodesWithQrByGymId(Long gymId, int limit, int size) {
        Gym gym = gymOwnerService.getAccessibleGym(gymId);
        if (limit < 1 || limit > 500) {
            throw new BadRequestException("limit must be between 1 and 500");
        }

        List<VisitCode> codes = visitCodeRepository.findTop200ByGymAndIsUsedOrderByIdDesc(gym, false);
        return codes.stream()
                .limit(limit)
                .map(code -> toQrCodeImageResponse(code, size))
                .toList();
    }

    @Transactional
    public QrCodeImageResponse generateOneQrByGymId(Long gymId, int size) {
        Gym gym = gymOwnerService.getAccessibleGym(gymId);
        String code = generateUniqueCode(gym);

        VisitCode visitCode = new VisitCode();
        visitCode.setCode(code);
        visitCode.setGym(gym);
        visitCode.setBatch(null);
        visitCode.setIssuedToGymAt(LocalDateTime.now());
        visitCode.setUsed(false);
        visitCode.setUsedByUser(null);
        visitCode.setUsedAt(null);
        VisitCode saved = visitCodeRepository.save(visitCode);

        return toQrCodeImageResponse(saved, size);
    }

    @Transactional(readOnly = true)
    public byte[] getQrPngByCode(String code, int size) {
        VisitCode visitCode = visitCodeRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Visit code not found"));

        gymOwnerService.getAccessibleGym(visitCode.getGym().getId());
        return generateQrPng(code, size);
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

    private QrCodeImageResponse toQrCodeImageResponse(VisitCode visitCode, int size) {
        QrCodeImageResponse response = new QrCodeImageResponse();
        response.setGymId(visitCode.getGym().getId());
        response.setGymName(visitCode.getGym().getName());
        response.setCode(visitCode.getCode());
        response.setQrPngBase64(Base64.getEncoder().encodeToString(generateQrPng(visitCode.getCode(), size)));
        return response;
    }

    private byte[] generateQrPng(String content, int size) {
        int normalizedSize = Math.max(128, Math.min(size, 1024));
        try {
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, normalizedSize, normalizedSize);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            return outputStream.toByteArray();
        } catch (WriterException e) {
            throw new BadRequestException("Unable to generate QR code");
        } catch (java.io.IOException e) {
            throw new BadRequestException("Unable to generate QR PNG");
        }
    }
}
