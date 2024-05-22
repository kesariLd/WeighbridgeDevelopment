package com.weighbridge.qualityuser.controller;

import com.weighbridge.qualityuser.payloads.QualityCreationResponse;
import com.weighbridge.qualityuser.payloads.QualityDashboardPaginationResponse;
import com.weighbridge.qualityuser.payloads.QualityDashboardResponse;
import com.weighbridge.qualityuser.payloads.ReportResponse;
import com.weighbridge.qualityuser.services.QualityTransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    public ResponseEntity<QualityDashboardPaginationResponse> getAllTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        QualityDashboardPaginationResponse response = qualityTransactionService.getAllGateDetails(pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Add quality checks to the transaction.
     *
     * @param ticketNo the ticket number for the quality wil be checked
     * @param transactionRequest the request object containing quality information for the transaction
     * @return a ResponseEntity containing the success message with HTTP status code 201(CREATED)
     */
    @PostMapping("/{ticketNo}")
    public ResponseEntity<String> createQualityTransaction(@PathVariable Integer ticketNo, @RequestBody Map<String, Double> transactionRequest) {
        String response = qualityTransactionService.createQualityTransaction(ticketNo, transactionRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Generates a quality report for the given ticket number.
     *
     * @param ticketNo the ticket number for the quality report
     * @return a ResponseEntity containing the generated quality report as a byte array with
     */

    @GetMapping("/report-response/{ticketNo}")
    public ResponseEntity<ReportResponse> checkReportResponse(@PathVariable Integer ticketNo) {
        ReportResponse reportResponse = qualityTransactionService.getReportResponse(ticketNo);
        if (reportResponse == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }


        return ResponseEntity.ok(reportResponse);
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
