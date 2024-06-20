package com.weighbridge.weighbridgeoperator.controllers;

import com.weighbridge.weighbridgeoperator.dto.WeighbridgeOperatorSearchCriteria;
import com.weighbridge.weighbridgeoperator.payloads.WeighbridgePageResponse;
import com.weighbridge.weighbridgeoperator.payloads.WeighmentTransactionResponse;
import com.weighbridge.weighbridgeoperator.services.WeighmentSearchApiService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/search/v1/Api")
public class WeighmentSearchApi {

    @Autowired
    private WeighmentSearchApiService weighmentSearchApiService;


    @GetMapping("/searchApi/{ticketNo}")
    public ResponseEntity<WeighmentTransactionResponse> searchByTicketNo(@PathVariable Integer ticketNo) {
        WeighmentTransactionResponse byTicketNo = weighmentSearchApiService.getByTicketNo(ticketNo);
        return ResponseEntity.ok(byTicketNo);
    }

    @GetMapping("/serachApi")
    public ResponseEntity<WeighbridgePageResponse> searchByVariable(@RequestParam(required = false) Integer ticketNo,
                                                                    @RequestParam(required = false) String transactionType,
                                                                    @RequestParam(required = false) LocalDate transactionDate,
                                                                    @RequestParam(required = false) String vehicleNo,
                                                                    @RequestParam(required = false) String supplierName,
                                                                    @RequestParam(required = false) String customerName,
                                                                    @RequestParam(required = false) String transporterName,
                                                                    @RequestParam(required = false) String materialName,
                                                                    @RequestParam(required = false) Boolean today,
                                                                    @RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "5") int size,@RequestParam String userId) {

        WeighbridgeOperatorSearchCriteria criteria = new WeighbridgeOperatorSearchCriteria();
        criteria.setTicketNo(ticketNo);
        criteria.setTransactionType(transactionType);
        criteria.setTransactionDate(transactionDate);
        criteria.setVehicleNo(vehicleNo);
        criteria.setSupplierName(supplierName);
        criteria.setCustomerName(customerName);
        criteria.setTransporterName(transporterName);
        criteria.setMaterialName(materialName);
        criteria.setToday(today);
        Pageable pageable = PageRequest.of(page, size);
        WeighbridgePageResponse allBySearchFields = weighmentSearchApiService.getAllBySearchFields(criteria, pageable,userId);
        return ResponseEntity.ok(allBySearchFields);
    }

    @GetMapping("/serachApi/Inprocess")
    public ResponseEntity<WeighbridgePageResponse> searchByVariableForInProcessTransaction(@RequestParam(required = false) Integer ticketNo,
                                                                    @RequestParam(required = false) String transactionType,
                                                                    @RequestParam(required = false) LocalDate transactionDate,
                                                                    @RequestParam(required = false) String vehicleNo,
                                                                    @RequestParam(required = false) String supplierName,
                                                                    @RequestParam(required = false) String customerName,
                                                                    @RequestParam(required = false) String transporterName,
                                                                    @RequestParam(required = false) String materialName,
                                                                    @RequestParam(required = false) Boolean today,
                                                                    @RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "5") int size,@RequestParam String userId) {

        WeighbridgeOperatorSearchCriteria criteria = new WeighbridgeOperatorSearchCriteria();
        criteria.setTicketNo(ticketNo);
        criteria.setTransactionType(transactionType);
        criteria.setTransactionDate(transactionDate);
        criteria.setVehicleNo(vehicleNo);
        criteria.setSupplierName(supplierName);
        criteria.setCustomerName(customerName);
        criteria.setTransporterName(transporterName);
        criteria.setMaterialName(materialName);
        criteria.setToday(today);
        Pageable pageable = PageRequest.of(page, size);
        WeighbridgePageResponse allBySearchFields = weighmentSearchApiService.getAllBySearchFieldsForInprocessTransaction(criteria, pageable,userId);
        return ResponseEntity.ok(allBySearchFields);
    }

}