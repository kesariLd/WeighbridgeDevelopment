package com.weighbridge.qualityuser.controller;

import com.weighbridge.qualityuser.payloads.QualityRequest;
import com.weighbridge.qualityuser.payloads.QualityResponse;
import com.weighbridge.qualityuser.services.QualityTransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public ResponseEntity<List<QualityResponse>> getAllTickets() {
        List<QualityResponse> response = qualityTransactionService.getAllGateDetails();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{ticketNo}")
    public ResponseEntity<String> createQualityTransaction(@PathVariable Integer ticketNo, @RequestBody QualityRequest qualityRequest) {
        String response = qualityTransactionService.createQualityTransaction(ticketNo, qualityRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


}
