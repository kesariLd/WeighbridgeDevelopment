package com.weighbridge.qualityuser.controller;

import com.weighbridge.admin.services.MaterialMasterService;
import com.weighbridge.admin.services.ProductMasterService;
import com.weighbridge.qualityuser.payloads.QualityCreationResponse;
import com.weighbridge.qualityuser.payloads.QualityDashboardResponse;
import com.weighbridge.qualityuser.payloads.ReportResponse;
import com.weighbridge.qualityuser.services.QualityTransactionService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for handling quality transaction related operations.
 */
@RestController
@RequestMapping("api/v1/qualities")
public class QualityTransactionController {
    private final QualityTransactionService qualityTransactionService;
    private final ProductMasterService productMasterService;
    private final MaterialMasterService materialMasterService;

    /**
     * Constructor for QualityTransactionController
     *
     * @param qualityTransactionService the  service to handle quality transaction related operations
     */
    public QualityTransactionController(QualityTransactionService qualityTransactionService, ProductMasterService productMasterService, MaterialMasterService materialMasterService) {
        this.qualityTransactionService = qualityTransactionService;
        this.productMasterService = productMasterService;
        this.materialMasterService = materialMasterService;
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

    @GetMapping("/qct-completed")
    public ResponseEntity<List<QualityDashboardResponse>>getQCTCompleted(){
        List<QualityDashboardResponse> responses=qualityTransactionService.getQCTCompleted();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/inbound-qct-completed")
    public List<QualityDashboardResponse> getInboundQCTCompleted() {
        return qualityTransactionService.getQCTCompletedInbound();
    }

    @GetMapping("/outbound-qct-completed")
    public List<QualityDashboardResponse> getOutboundQCTCompleted() {
        return qualityTransactionService.getQCTCompletedOutbound();
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

    @PutMapping("{ticketNo}")
    public ResponseEntity<Void>passQualityTransaction(@PathVariable Integer ticketNo){
        qualityTransactionService.passQualityTransaction(ticketNo);
        return ResponseEntity.noContent().build();
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


}


