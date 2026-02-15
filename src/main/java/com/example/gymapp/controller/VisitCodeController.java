package com.example.gymapp.controller;

import com.example.gymapp.dto.ScanCodeRequest;
import com.example.gymapp.dto.ScanCodeResponse;
import com.example.gymapp.service.VisitService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/visit-codes")
public class VisitCodeController {

    private final VisitService visitService;

    public VisitCodeController(VisitService visitService) {
        this.visitService = visitService;
    }

    @PostMapping("/scan")
    @ResponseStatus(HttpStatus.OK)
    public ScanCodeResponse scan(@Valid @RequestBody ScanCodeRequest request) {
        return visitService.scanCode(request.getCode());
    }
}
