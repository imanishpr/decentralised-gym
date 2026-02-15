package com.example.gymapp.controller;

import com.example.gymapp.dto.VisitResponse;
import com.example.gymapp.service.VisitService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/visits")
public class VisitController {

    private final VisitService visitService;

    public VisitController(VisitService visitService) {
        this.visitService = visitService;
    }

    @GetMapping("/my-visits")
    public List<VisitResponse> getMyVisits() {
        return visitService.getMyVisits();
    }
}
