package com.weighbridge.qualityuser.controller;

import com.weighbridge.qualityuser.payloads.QualityResponse;
import com.weighbridge.qualityuser.services.QualityTransactionService;
import com.weighbridge.weighbridgeoperator.payloads.WeighmentTransactionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/qualities")
public class QualityTransactionController {

    private final QualityTransactionService qualityTransactionService;

    public QualityTransactionController(QualityTransactionService qualityTransactionService) {
        this.qualityTransactionService = qualityTransactionService;
    }

    @GetMapping("/getAllTransaction")
    public ResponseEntity<List<QualityResponse>> getAllTickets(){
        List<QualityResponse> response=qualityTransactionService.getAllGateDetails();
        return ResponseEntity.ok(response);
    }



}
