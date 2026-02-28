package com.example.gymapp.controller;

import com.example.gymapp.dto.AvailableQrCodeResponse;
import com.example.gymapp.dto.CreateQrBatchRequest;
import com.example.gymapp.dto.QrBatchResponse;
import com.example.gymapp.service.QrInventoryService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
}
