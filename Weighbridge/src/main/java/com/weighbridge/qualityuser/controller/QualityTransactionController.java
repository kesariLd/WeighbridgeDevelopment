package com.weighbridge.qualityuser.controller;

import com.weighbridge.qualityuser.payloads.QualityCreationResponse;
import com.weighbridge.qualityuser.payloads.QualityRequest;
import com.weighbridge.qualityuser.payloads.QualityDashboardResponse;
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

/**
 * Controller for handling quality transaction related operations.
 */
@RestController
@RequestMapping("api/v1/qualities")
public class QualityTransactionController {
    private final QualityTransactionService qualityTransactionService;

    /**
     * Constructor for QualityTransactionController
     *
     * @param qualityTransactionService the  service to handle quality transaction related operations
     */
    public QualityTransactionController(QualityTransactionService qualityTransactionService) {
        this.qualityTransactionService = qualityTransactionService;
    }

    /**
     * Retrieves all gate entry transaction details which quality will be measured.
     *
     * @return a ResponseEntity containing a list of all gate entry transaction details
     */
    @GetMapping("/getAllTransaction")
    public ResponseEntity<List<QualityDashboardResponse>> getAllTickets() {
        List<QualityDashboardResponse> response = qualityTransactionService.getAllGateDetails();
        return ResponseEntity.ok(response);
    }

    /**
     * Add quality checks to the transaction.
     *
     * @param ticketNo the ticket number for the quality wil be checked
     * @param qualityRequest the request object containing quality information for the transaction
     * @return a ResponseEntity containing the success message with HTTP status code 201(CREATED)
     */
    @PostMapping("/{ticketNo}")
    public ResponseEntity<String> createQualityTransaction(@PathVariable Integer ticketNo, @RequestBody QualityRequest qualityRequest) {
        String response = qualityTransactionService.createQualityTransaction(ticketNo, qualityRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Generates a quality report for the given ticket number.
     *
     * @param ticketNo the ticket number for the quality report
     * @return a ResponseEntity containing the generated quality report as a byte array with
     */
    @GetMapping("/generate-report/{ticketNo}")
    public ResponseEntity<byte[]> generateQualityReport(@PathVariable Integer ticketNo) {
        ReportResponse reportResponse = qualityTransactionService.getReportResponse(ticketNo);
        if (reportResponse != null) {
            byte[] reportBytes = qualityTransactionService.generateQualityReport(reportResponse);
            if (reportBytes != null) {
                return ResponseEntity.ok()
                        .header("Content-Disposition", "attachment; filename=quality_report.pdf")
                        .body(reportBytes);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Rerieves a quality details for the given ticket number.
     *
     * @param ticketNo the ticket number for the quality transaction details
     * @return a ResponseEntity containing the quality details
     */
    @GetMapping("/{ticketNo}")
    public ResponseEntity<QualityCreationResponse> getDetailsForQualityTransactions(@PathVariable Integer ticketNo) {
        QualityCreationResponse qualityCreationResponse = qualityTransactionService.getDetailsForQualityTransaction(ticketNo);
        return ResponseEntity.ok(qualityCreationResponse);
    }
}
