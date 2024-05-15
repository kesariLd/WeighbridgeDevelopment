package com.weighbridge.weighbridgeoperator.controllers;

import com.weighbridge.weighbridgeoperator.payloads.WeighbridgeReportResponse;
import com.weighbridge.weighbridgeoperator.services.WeighmentReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/weighment")
public class WeighmentReportController {

    @Autowired
    private WeighmentReportService weighmentReportService;

    @GetMapping("/report")
    public ResponseEntity<List<WeighbridgeReportResponse>> generateWeighmentReport2(@RequestParam(required = false) LocalDate startDate,
                                                                                    @RequestParam(required = false) LocalDate endDate) {
        System.out.println("startDate" +startDate+"endDate "+endDate);

        return ResponseEntity.ok(weighmentReportService.generateWeighmentReport(startDate,endDate));
    }
}
