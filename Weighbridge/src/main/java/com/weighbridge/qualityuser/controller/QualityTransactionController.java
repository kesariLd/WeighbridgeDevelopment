package com.weighbridge.qualityuser.controller;

import com.weighbridge.qualityuser.payloads.QualityRequest;
import com.weighbridge.qualityuser.payloads.QualityResponse;
import com.weighbridge.qualityuser.payloads.ReportResponse;
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

    @GetMapping("/generate-report/{ticketNo}")
    public ResponseEntity<byte[]> generateQualityReport(@PathVariable Integer ticketNo) {
        // Assuming you have a method to fetch the report response based on the ticketNo
        ReportResponse reportResponse = qualityTransactionService.getReportResponse(ticketNo);
        if (reportResponse != null) {
            byte[] reportBytes = qualityTransactionService.generateQualityReport(reportResponse);
            if (reportBytes != null) {
                return ResponseEntity.ok()
                        .header("Content-Disposition", "attachment; filename=quality_report.pdf")
                        .body(reportBytes);
            } else {
                // Handle the case where reportBytes is null (report generation failed)
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        } else {
            // Handle the case where reportResponse is null (no data found for the given ticketNo)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }



}
