package com.weighbridge.management.controllers;

import com.weighbridge.management.payload.ManagementQualityDashboardResponse;
import com.weighbridge.management.services.ManagementDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/api/v1/management")
public class ManagementSearchController {


    private final ManagementDashboardService managementDashboardService;

    public ManagementSearchController(ManagementDashboardService managementDashboardService) {
        this.managementDashboardService = managementDashboardService;
    }


    @GetMapping("/searchByTicketNo")
    public ResponseEntity<ManagementQualityDashboardResponse> searchByTicketNo(
            @RequestParam String ticketNo,
            @RequestParam String companyName,
            @RequestParam String siteName) {

        ManagementQualityDashboardResponse responses = managementDashboardService.searchByTicketNo(ticketNo,companyName,siteName);
        return ResponseEntity.ok(responses);
    }
}

