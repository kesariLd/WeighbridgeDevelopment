package com.weighbridge.weighbridgeoperator.controllers;

import com.weighbridge.weighbridgeoperator.payloads.WeighmentReportResponse;
import com.weighbridge.weighbridgeoperator.services.WeighmentReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/weighment")
public class WeighmentReportController {

    @Autowired
    private WeighmentReportService weighmentReportService;
    @GetMapping("/report")
    public ResponseEntity< Map<String, Map<String, List<WeighmentReportResponse>>>> generateWeighmentReport(@RequestParam(required = false) LocalDate startDate,
                                                                                                            @RequestParam(required = false) LocalDate endDate) {
        System.out.println("startDate" +startDate+"endDate "+endDate);
        Map<String, Map<String, List<WeighmentReportResponse>>> report = weighmentReportService.generateWeighmentReport(startDate,endDate);
        return ResponseEntity.ok(report);
    }
}
