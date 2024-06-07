package com.weighbridge.management.controllers;


import com.weighbridge.gateuser.payloads.GateEntryTransactionPageResponse;
import com.weighbridge.management.payload.ManagementGateEntryList;
import com.weighbridge.management.payload.ManagementGateEntryTransactionResponse;
import com.weighbridge.qualityuser.payloads.QualityDashboardResponse;
import com.weighbridge.qualityuser.services.QualityTransactionService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.weighbridge.management.payload.ManagementPayload;
import com.weighbridge.management.payload.MaterialProductDataResponse;
import com.weighbridge.management.services.ManagementDashboardService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/management")
public class ManagementDashboardController {

    private final ManagementDashboardService managementDashboardService;
    private final QualityTransactionService qualityTransactionService;

    public ManagementDashboardController(ManagementDashboardService managementDashboardService, QualityTransactionService qualityTransactionService) {
        this.managementDashboardService = managementDashboardService;
        this.qualityTransactionService = qualityTransactionService;
    }

    // bar chart for the material or product received data wise
    @PostMapping("/material-product")
    public ResponseEntity<MaterialProductDataResponse> materialProductBarChartDataResponse(@RequestBody ManagementPayload managementRequest) {
        MaterialProductDataResponse response = managementDashboardService.getMaterialProductBarChartData(managementRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/qct-completed")
    public ResponseEntity<List<QualityDashboardResponse>> getQCTCompleted(){
        List<QualityDashboardResponse> responses=qualityTransactionService.getQCTCompleted();
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/gate-dash")
    public ResponseEntity<List<Map<String, Object>>> getManagementGateEntryDashboard(@RequestBody ManagementPayload managementRequest) {
        List<Map<String, Object>> data = managementDashboardService.managementGateEntryDashboard(managementRequest);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }
    @GetMapping("/transactions/ongoing")
    public ManagementGateEntryList getTransactionsOngoing(
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "5", required = false) int size,
            @RequestParam(required = false, defaultValue = "ticketNo") String sortField,
            @RequestParam(defaultValue = "desc", required = false) String sortOrder,
            @RequestParam(required = false) Integer ticketNo,
            @RequestParam(required = false) String vehicleNo,
            @RequestParam(required = false , defaultValue = "ongoing") String vehicleStatus,
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) String siteName,
            @RequestParam(required = false) String transactionType,
            @RequestParam(required = false) String supplierName,
            @RequestParam(required = false) LocalDate date) {
        Pageable pageable;
        if(sortField!=null && !sortField.isEmpty()){
            Sort.Direction direction = sortOrder.equalsIgnoreCase("desc")?Sort.Direction.DESC:Sort.Direction.ASC;
            Sort sort = Sort.by(direction,sortField);
            pageable = PageRequest.of(page,size,sort);
        }
        else{
            pageable = PageRequest.of(page,size);
        }
        ManagementGateEntryList managementGateEntryList = managementDashboardService.gateEntryList(ticketNo, vehicleNo, date, supplierName, transactionType, pageable, vehicleStatus,companyName, siteName);
        return managementGateEntryList;
    }
}
