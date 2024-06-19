package com.weighbridge.weighbridgeoperator.controllers;

import com.weighbridge.weighbridgeoperator.payloads.WeighbridgeReportResponse;
import com.weighbridge.weighbridgeoperator.payloads.WeighmentPrintResponse;
import com.weighbridge.weighbridgeoperator.services.WeighmentReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Rest Controller class for handling weighment report related API requests.
 */
@RestController
@RequestMapping("/api/v1/weighment")
public class WeighmentReportController {

    /**
     * Autowired instance of WeighmentReportService for generating weighment reports.
     */
    @Autowired
    private WeighmentReportService weighmentReportService;

    /**
     * Generates a weighment report based on optional start and end dates.
     *
     * @param startDate The starting date for the report (format: YYYY-MM-DD), optional.
     * @param endDate The ending date for the report (format: YYYY-MM-DD), optional.
     * @return ResponseEntity containing a list of WeighbridgeReportResponse objects
     *         representing the weighment report data.
     */
    @GetMapping("/report")
    public ResponseEntity<List<WeighbridgeReportResponse>> generateWeighmentReport2(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) String siteName,
            @RequestParam(required = false) String userId
            
    ) {
        System.out.println("startDate" + startDate + " endDate " + endDate);

        return ResponseEntity.ok(weighmentReportService.generateWeighmentReport(startDate, endDate,companyName,siteName,userId));
    }

    @GetMapping("/transactions/print/{ticketNo}")
    public ResponseEntity<WeighmentPrintResponse> printWeighmentTransaction(@PathVariable Integer ticketNo,@RequestParam String userId) {
        WeighmentPrintResponse  weighmentPrintResponse = weighmentReportService.getAllWeighmentTransactions(ticketNo,userId);
        return ResponseEntity.ok(weighmentPrintResponse);
    }
    @GetMapping("/getReport")
    public ResponseEntity<List<Map<String, Object>>> getDemoData(@RequestBody List<String> selectedFields, @RequestParam(required = false) LocalDate startDate,
                                                                 @RequestParam(required = false) LocalDate endDate,@RequestParam String userId) {

        return new ResponseEntity<>(weighmentReportService.generateCustomizedReport(selectedFields,startDate,endDate,userId), HttpStatus.OK);
    }
}