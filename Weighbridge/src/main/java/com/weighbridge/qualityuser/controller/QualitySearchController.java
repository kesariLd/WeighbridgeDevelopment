package com.weighbridge.qualityuser.controller;

import com.weighbridge.admin.services.MaterialMasterService;
import com.weighbridge.admin.services.ProductMasterService;
import com.weighbridge.qualityuser.payloads.QualityDashboardResponse;
import com.weighbridge.qualityuser.services.QualityTransactionSearchService;
import com.weighbridge.qualityuser.services.QualityTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<QualityDashboardResponse> searchByTicketNo(@PathVariable Integer ticketNo) {
        QualityDashboardResponse response = qualityTransactionSearchService.searchByTicketNo(ticketNo);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/searchByVehicleNo/{vehicleNo}")
    public ResponseEntity<List<QualityDashboardResponse>> searchByVehicleNo(@PathVariable String vehicleNo) {
        List<QualityDashboardResponse> response = qualityTransactionSearchService.searchByVehicleNo(vehicleNo);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/searchBySupplierOrCustomer")
    public ResponseEntity<List<QualityDashboardResponse>> searchBySupplierOrCustomerNameAndAddress(
            @RequestParam(required = false) String supplierOrCustomerName,
            @RequestParam(required = false) String supplierOrCustomerAddress) {
        List<QualityDashboardResponse> response = qualityTransactionSearchService.searchBySupplierOrCustomerNameAndAddress(supplierOrCustomerName, supplierOrCustomerAddress);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/search-Date")
    public ResponseEntity<List<QualityDashboardResponse>> searchByDate(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") String date
    ) {
        if (date != null) {
            List<QualityDashboardResponse> response = qualityTransactionSearchService.searchByDate(date);
            return ResponseEntity.ok().body(response);
        } else {

            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
    }

}
