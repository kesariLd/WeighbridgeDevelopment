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
    @GetMapping("/{ticketNo}")
    public ResponseEntity<QualityCreationResponse> getDetailsForQualityTransactions(@PathVariable Integer ticketNo) {
        QualityCreationResponse qualityCreationResponse = qualityTransactionService.getDetailsForQualityTransaction(ticketNo);
        return ResponseEntity.ok(qualityCreationResponse);
    }


    @GetMapping("/searchByTicketNo/{ticketNo}")
    public ResponseEntity<QualityDashboardResponse> searchByTicketNo(@PathVariable Integer ticketNo) {
        QualityDashboardResponse response = qualityTransactionService.searchByTicketNo(ticketNo);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/searchByVehicleNo/{vehicleNo}")
    public ResponseEntity<List<QualityDashboardResponse>> searchByVehicleNo(@PathVariable String vehicleNo) {
        List<QualityDashboardResponse> response = qualityTransactionService.searchByVehicleNo(vehicleNo);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/searchBySupplierOrCustomer")
    public ResponseEntity<List<QualityDashboardResponse>> searchBySupplierOrCustomerNameAndAddress(
            @RequestParam(required = false) String supplierOrCustomerName,
            @RequestParam(required = false) String supplierOrCustomerAddress) {
        List<QualityDashboardResponse> response = qualityTransactionService.searchBySupplierOrCustomerNameAndAddress(supplierOrCustomerName, supplierOrCustomerAddress);
        return ResponseEntity.ok().body(response);
    }


    /**
     * Handles GET requests to search for quality dashboard entries based on various optional criteria.
     *
     * @param ticketNo An optional parameter representing the ticket number to search for.
     *                 If provided, the search will include entries matching this ticket number.
     * @param vehicleNo An optional parameter representing the vehicle number to search for.
     *                  If provided, the search will include entries matching this vehicle number.
     * @param supplierOrCustomerName An optional parameter representing the supplier or customer name to search for.
     *                               If provided, the search will include entries matching this name.
     * @param supplierOrCustomerAddress An optional parameter representing the supplier or customer address to search for.
     *                                  If provided, the search will include entries matching this address.
     * @return A ResponseEntity containing a list of QualityDashboardResponse objects that match the provided search criteria.
     *         The response has an HTTP status of 200 (OK) if the search is successful.
     */
    @GetMapping("/search")
    public ResponseEntity<List<QualityDashboardResponse>>searchByTicketNoVehicleNoSupplierAndSupplierAddress(
            @RequestParam(required = false) Integer ticketNo,
            @RequestParam (required = false)String vehicleNo,
            @RequestParam (required = false)String supplierOrCustomerName,
            @RequestParam(required = false) String supplierOrCustomerAddress
    ){
        List<QualityDashboardResponse> response=qualityTransactionService.searchByTicketNoVehicleNoSupplierAndSupplierAddress(ticketNo,vehicleNo,supplierOrCustomerName,supplierOrCustomerAddress);
        return ResponseEntity.ok().body(response);
    }


    @GetMapping("/search-Date")
    public ResponseEntity<List<QualityDashboardResponse>> searchByDate(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") String date
    ) {
        if (date != null) {
            List<QualityDashboardResponse> response = qualityTransactionService.searchByDate(date);
            return ResponseEntity.ok().body(response);
        } else {

            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
    }
  @GetMapping("fetch-ProductsOrMaterials")
    public ResponseEntity<List<String>> getProductsOrMaterials(@RequestParam String type){
        if("product".equalsIgnoreCase(type)){
            List<String> products=productMasterService.getAllProductNames();
            return ResponseEntity.ok(products);
        } else if ("material".equalsIgnoreCase(type)) {
            List<String> materials=materialMasterService.getAllMaterialNames();
            return ResponseEntity.ok(materials);
        }
        return ResponseEntity.badRequest().body(List.of("Invalid parameter"));
  }

}


