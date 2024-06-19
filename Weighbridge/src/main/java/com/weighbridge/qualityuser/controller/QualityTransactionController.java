package com.weighbridge.qualityuser.controller;

import com.weighbridge.admin.services.MaterialMasterService;
import com.weighbridge.admin.services.ProductMasterService;
import com.weighbridge.qualityuser.payloads.QualityDashboardResponse;
import com.weighbridge.qualityuser.payloads.ReportResponse;
import com.weighbridge.qualityuser.services.QualityTransactionService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

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
    public QualityTransactionController(QualityTransactionService qualityTransactionService,
                                        ProductMasterService productMasterService,
                                        MaterialMasterService materialMasterService) {
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
    public ResponseEntity<List<QualityDashboardResponse>> getAllTickets(@RequestParam String userId) {
        List<QualityDashboardResponse> response = qualityTransactionService.getAllGateDetails(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/qct-completed")
    public ResponseEntity<List<QualityDashboardResponse>>getQCTCompleted(@RequestParam String userId){
        List<QualityDashboardResponse> responses=qualityTransactionService.getQCTCompleted(userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/inbound-qct-completed")
    public List<QualityDashboardResponse> getInboundQCTCompleted(@RequestParam String userId) {
        return qualityTransactionService.getQCTCompletedInbound(userId);
    }

    @GetMapping("/outbound-qct-completed")
    public List<QualityDashboardResponse> getOutboundQCTCompleted(@RequestParam String userId) {
        return qualityTransactionService.getQCTCompletedOutbound(userId);
    }

    /**
     * Add quality checks to the transaction.
     *
     * @param ticketNo the ticket number for the quality wil be checked
     * @param transactionRequest the request object containing quality information for the transaction
     * @return a ResponseEntity containing the success message with HTTP status code 201(CREATED)
     */
    @PostMapping("/{ticketNo}")
    public ResponseEntity<String> createQualityTransaction(@PathVariable Integer ticketNo,
                                                           @RequestParam String userId,
                                                           @RequestBody Map<String, Double> transactionRequest) {
        String response = qualityTransactionService.createQualityTransaction(ticketNo, userId, transactionRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("{ticketNo}")
    public ResponseEntity<Void>passQualityTransaction(@PathVariable Integer ticketNo, @RequestParam String userId){
        qualityTransactionService.passQualityTransaction(ticketNo, userId);
        return ResponseEntity.noContent().build();
    }


    /**
     * Generates a quality report for the given ticket number.
     *
     * @param ticketNo the ticket number for the quality report
     * @return a ResponseEntity containing the generated quality report as a byte array with
     */

    @GetMapping("/report-response/{ticketNo}")
    public ResponseEntity<ReportResponse> checkReportResponse(@PathVariable Integer ticketNo, @RequestParam String userId) {
        ReportResponse reportResponse = qualityTransactionService.getReportResponse(ticketNo, userId);
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


