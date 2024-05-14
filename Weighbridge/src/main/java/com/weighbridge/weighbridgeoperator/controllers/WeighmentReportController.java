package com.weighbridge.weighbridgeoperator.controllers;

import com.weighbridge.weighbridgeoperator.payloads.WeighmentReportResponse;
import com.weighbridge.weighbridgeoperator.services.WeighmentReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/weighment")
public class WeighmentReportController {

    @Autowired
    private WeighmentReportService weighmentReportService;
    @GetMapping("/report/{startDate}/{endDate}")
    public ResponseEntity< Map<String, Map<String, List<WeighmentReportResponse>>>> generateWeighmentReport(@PathVariable LocalDate startDate,LocalDate endDate) {
        Map<String, Map<String, List<WeighmentReportResponse>>> report = weighmentReportService.generateWeighmentReport(startDate,endDate);
        return ResponseEntity.ok(report);
    }
}
