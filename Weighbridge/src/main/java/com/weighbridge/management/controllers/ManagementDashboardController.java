package com.weighbridge.management.controllers;

import com.weighbridge.qualityuser.payloads.QualityDashboardResponse;
import com.weighbridge.qualityuser.services.QualityTransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/management")
public class ManagementDashboardController {

    private final QualityTransactionService qualityTransactionService;

    public ManagementDashboardController(QualityTransactionService qualityTransactionService) {
        this.qualityTransactionService = qualityTransactionService;
    }

    @GetMapping("/qct-completed")
    public ResponseEntity<List<QualityDashboardResponse>> getQCTCompleted(){
        List<QualityDashboardResponse> responses=qualityTransactionService.getQCTCompleted();
        return ResponseEntity.ok(responses);
    }
}