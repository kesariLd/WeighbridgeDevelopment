package com.weighbridge.management.controllers;


import com.weighbridge.management.payload.ManagementQualityDashboardResponse;
import com.weighbridge.management.payload.AllTransactionResponse;

import com.weighbridge.management.payload.CoalMoisturePercentageRequest;
import com.weighbridge.management.payload.CoalMoisturePercentageResponse;
import com.weighbridge.gateuser.payloads.GateEntryTransactionPageResponse;
import com.weighbridge.management.payload.ManagementGateEntryList;
import com.weighbridge.management.payload.ManagementGateEntryTransactionResponse;

import com.weighbridge.management.payload.MaterialProductQualityResponse;
import com.weighbridge.qualityuser.entites.QualityTransaction;
import com.weighbridge.management.dtos.WeightResponseForGraph;
import com.weighbridge.qualityuser.payloads.QualityDashboardResponse;
import com.weighbridge.qualityuser.services.QualityTransactionService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
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

    public ManagementDashboardController(ManagementDashboardService managementDashboardService) {
        this.managementDashboardService = managementDashboardService;
    }

    // bar chart for the material or product received data wise
    @PostMapping("/material-product")
    public ResponseEntity<MaterialProductDataResponse> materialProductBarChartDataResponse(@RequestBody ManagementPayload managementRequest) {
        MaterialProductDataResponse response = managementDashboardService.getMaterialProductBarChartData(managementRequest);
        return ResponseEntity.ok(response);
    }

   //bar chart for good quality
    @PostMapping("/material-product/qualities")
    public ResponseEntity<MaterialProductQualityResponse> getMaterialProductQualities(@RequestBody ManagementPayload managementRequest){
        MaterialProductQualityResponse response =managementDashboardService.getMaterialProductQualities(managementRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/completedQualities/GoodOrBad")
    public ResponseEntity<List<ManagementQualityDashboardResponse>> getGoodOrBadQualities(
            @RequestBody ManagementPayload managementRequest,
            @RequestParam(required = false) String transactionType,
            @RequestParam(required = false) String qualityType)
             {
        List<ManagementQualityDashboardResponse> response = managementDashboardService.getGoodOrBadQualities(managementRequest, transactionType, qualityType);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/goodQualities")
    public ResponseEntity<List<ManagementQualityDashboardResponse>> getGoodQualities(
            @RequestBody ManagementPayload managementRequest,
            @RequestParam(required = false) String transactionType){
        List<ManagementQualityDashboardResponse> responses=managementDashboardService.getGoodQualities(managementRequest,transactionType);
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/badQualities")
    public ResponseEntity<List<ManagementQualityDashboardResponse>> getBadQualities(
            @RequestBody ManagementPayload managementRequest,
            @RequestParam(required = false) String transactionType){
        List<ManagementQualityDashboardResponse> responses=managementDashboardService.getBadQualities(managementRequest,transactionType);
        return ResponseEntity.ok(responses);
    }
    @PostMapping("/moisture-percentage")
    public ResponseEntity<CoalMoisturePercentageResponse> getMoisturePercentage(@RequestBody CoalMoisturePercentageRequest coalMoisturePercentageRequest){
        CoalMoisturePercentageResponse response =managementDashboardService.getMoisturePercentage(coalMoisturePercentageRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/getQtyByGraph")
    public ResponseEntity<List<WeightResponseForGraph>> getQtyResponseAsGraph(@RequestBody ManagementPayload managementPayload,@RequestParam String transactionType){
        List<WeightResponseForGraph> qtyResponseInGraph = managementDashboardService.getQtyResponseInGraph(managementPayload,transactionType);
        return ResponseEntity.ok(qtyResponseInGraph);
    }

    @PostMapping("/gate-dash")
    public ResponseEntity<List<Map<String, Object>>> getManagementGateEntryDashboard(@RequestBody ManagementPayload managementRequest) {
        List<Map<String, Object>> data = managementDashboardService.managementGateEntryDashboard(managementRequest);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }


    @PostMapping("/getAlltransaction")
    public  ResponseEntity<AllTransactionResponse> getAlltransactionResponse(@RequestBody ManagementPayload managementPayload,@RequestParam String transactionType){
        AllTransactionResponse allTransactionResponse = managementDashboardService.getAllTransactionResponse(managementPayload,transactionType);
        return ResponseEntity.ok(allTransactionResponse);
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

