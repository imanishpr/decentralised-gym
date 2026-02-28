package com.example.gymapp.controller;

import com.example.gymapp.dto.AvailableQrCodeResponse;
import com.example.gymapp.dto.CreateQrBatchRequest;
import com.example.gymapp.dto.QrCodeImageResponse;
import com.example.gymapp.dto.QrBatchResponse;
import com.example.gymapp.service.QrInventoryService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/qr")
@PreAuthorize("hasAnyRole('GYM_OWNER','ADMIN')")
public class QrController {

    private final QrInventoryService qrInventoryService;

    public QrController(QrInventoryService qrInventoryService) {
        this.qrInventoryService = qrInventoryService;
    }

    @PostMapping("/batch/create")
    public QrBatchResponse createBatch(@Valid @RequestBody CreateQrBatchRequest request) {
        return qrInventoryService.createBatch(request);
    }

    @GetMapping("/batch/{id}")
    public QrBatchResponse getBatch(@PathVariable("id") Long id) {
        return qrInventoryService.getBatch(id);
    }

    @GetMapping("/available")
    public List<AvailableQrCodeResponse> getAvailableCodes() {
        return qrInventoryService.getAvailableCodes();
    }

    @GetMapping("/gym/{gymId}/available")
    public List<AvailableQrCodeResponse> getAvailableCodesByGym(@PathVariable Long gymId) {
        return qrInventoryService.getAvailableCodesByGymId(gymId);
    }

    @GetMapping("/gym/{gymId}/available/with-qr")
    public List<QrCodeImageResponse> getAvailableCodesWithQrByGym(
            @PathVariable Long gymId,
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(defaultValue = "300") int size
    ) {
        return qrInventoryService.getAvailableCodesWithQrByGymId(gymId, limit, size);
    }

    @GetMapping(value = "/code/{code}/qr.png", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getQrPngByCode(
            @PathVariable String code,
            @RequestParam(defaultValue = "300") int size
    ) {
        byte[] png = qrInventoryService.getQrPngByCode(code, size);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(png);
    }

    @PostMapping("/gym/{gymId}/generate-one")
    public QrCodeImageResponse generateOneQrByGym(
            @PathVariable Long gymId,
            @RequestParam(defaultValue = "300") int size
    ) {
        return qrInventoryService.generateOneQrByGymId(gymId, size);
    }
}
