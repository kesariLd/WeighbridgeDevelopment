package com.weighbridge.qualityuser.controller;

import com.weighbridge.qualityuser.payloads.QualityDashboardResponse;
import com.weighbridge.qualityuser.services.QualityTransactionSearchService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("api/v1/qualities")
public class QualitySearchController {
    private final QualityTransactionSearchService qualityTransactionSearchService;

    public QualitySearchController(QualityTransactionSearchService qualityTransactionSearchService) {
        this.qualityTransactionSearchService = qualityTransactionSearchService;
    }

    @GetMapping("/searchByTicketNo/{ticketNo}")
    public ResponseEntity<QualityDashboardResponse> searchByTicketNo(@PathVariable Integer ticketNo,
                                                                     @RequestParam String userId,
                                                                     @RequestParam(defaultValue = "true") boolean checkQualityCompleted) {
        QualityDashboardResponse response = qualityTransactionSearchService.searchByTicketNo(ticketNo, userId, checkQualityCompleted);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/searchByVehicleNo/{vehicleNo}")
    public ResponseEntity<List<QualityDashboardResponse>> searchByVehicleNo(@PathVariable String vehicleNo, @RequestParam String userId) {
        List<QualityDashboardResponse> response = qualityTransactionSearchService.searchByVehicleNo(vehicleNo, userId);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/searchByVehicleNo-qctCompleted/{vehicleNo}")
    public ResponseEntity<List<QualityDashboardResponse>> searchByQCTCompletedVehicleNo(@PathVariable String vehicleNo, @RequestParam String userId) {
        List<QualityDashboardResponse> response = qualityTransactionSearchService.searchByQCTCompletedVehicleNo(vehicleNo, userId);
        return ResponseEntity.ok().body(response);
    }


    @GetMapping("/searchBySupplierOrCustomer")
    public ResponseEntity<List<QualityDashboardResponse>> searchBySupplierOrCustomerNameAndAddress(
            @RequestParam(required = false) String supplierOrCustomerName,
            @RequestParam(required = false) String supplierOrCustomerAddress,
            @RequestParam String userId) {
        List<QualityDashboardResponse> response = qualityTransactionSearchService.searchBySupplierOrCustomerNameAndAddress(supplierOrCustomerName, supplierOrCustomerAddress, userId);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("searchBySupplierOrCustomer-qctCompleted")
    public ResponseEntity<List<QualityDashboardResponse>> searchBySupplierOrCustomerNameAndAddressQctCompleted(
            @RequestParam(required = false) String supplierOrCustomerName,
            @RequestParam(required = false) String supplierOrCustomerAddress,
            @RequestParam String userId) {
        List<QualityDashboardResponse> response = qualityTransactionSearchService.searchBySupplierOrCustomerNameAndAddressQctCompleted(supplierOrCustomerName, supplierOrCustomerAddress, userId);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/search-Date")
    public ResponseEntity<List<QualityDashboardResponse>> searchByDate(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") String date,
            @RequestParam String userId) {
        if (date != null) {
            List<QualityDashboardResponse> response = qualityTransactionSearchService.searchByDate(date, userId);
            return ResponseEntity.ok().body(response);
        } else {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
    }

}
