package com.weighbridge.management.controllers;

import com.weighbridge.management.payload.MaterialProductQualityResponse;
import com.weighbridge.qualityuser.entites.QualityTransaction;
import com.weighbridge.management.dtos.WeightResponseForGraph;
import com.weighbridge.qualityuser.payloads.QualityDashboardResponse;
import com.weighbridge.qualityuser.services.QualityTransactionService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import com.weighbridge.management.payload.ManagementPayload;
import com.weighbridge.management.payload.MaterialProductDataResponse;
import com.weighbridge.management.services.ManagementDashboardService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/getQtyByGraph")
    public ResponseEntity<List<WeightResponseForGraph>> getQtyResponseAsGraph(@RequestBody ManagementPayload managementPayload){
        List<WeightResponseForGraph> qtyResponseInGraph = managementDashboardService.getQtyResponseInGraph(managementPayload);
        return ResponseEntity.ok(qtyResponseInGraph);
    }

    @PostMapping("/gate-dash")
    public ResponseEntity<List<Map<String, Object>>> getManagementGateEntryDashboard(@RequestBody ManagementPayload managementRequest) {
        List<Map<String, Object>> data = managementDashboardService.managementGateEntryDashboard(managementRequest);
        return new ResponseEntity<>(data, HttpStatus.OK);

    }

}
