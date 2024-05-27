package com.weighbridge.gateuser.controllers;

import com.weighbridge.gateuser.entities.GateEntryTransaction;
import com.weighbridge.gateuser.payloads.GateEntryTransactionPageResponse;
import com.weighbridge.gateuser.payloads.GateEntryTransactionRequest;
import com.weighbridge.gateuser.payloads.GateEntryTransactionResponse;
import com.weighbridge.gateuser.services.GateEntryTransactionService;
import org.hibernate.query.SortDirection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller class for GateEntryTransactionController
 */
@RestController
@RequestMapping("/api/v1/gate")
public class GateEntryTransactionController {

    @Autowired
    private GateEntryTransactionService gateEntryTransactionService;

    /**
     * Save a gate entry transaction.
     *
     * @param gateEntryTransactionRequest The gate entry transaction details to be saved.
     * @return The ID of the saved gate entry transaction.
     */
    @PostMapping
    public ResponseEntity<Integer> saveTransaction(@RequestBody GateEntryTransactionRequest gateEntryTransactionRequest) {
        Integer gateEntryResponse = gateEntryTransactionService.saveGateEntryTransaction(gateEntryTransactionRequest);
        return new ResponseEntity<>(gateEntryResponse, HttpStatus.OK);
    }

    /**
     * Get all gate entry transactions.
     *
     * @return A list of all gate entry transactions.
     */
    @GetMapping
    public ResponseEntity<GateEntryTransactionPageResponse> getAllTransaction( @RequestParam(defaultValue = "0", required = false) int page,
                                                                                 @RequestParam(defaultValue = "5", required = false) int size,
                                                                                 @RequestParam(required = false, defaultValue = "ticketNo") String sortField,
                                                                                 @RequestParam(defaultValue = "desc", required = false) String sortOrder) {
        Pageable pageable;
        if(sortField!=null && !sortField.isEmpty()){
            Sort.Direction direction = sortOrder.equalsIgnoreCase("desc")?Sort.Direction.DESC:Sort.Direction.ASC;
            Sort sort = Sort.by(direction,sortField);
            pageable = PageRequest.of(page,size,sort);
        }
        else{
            pageable = PageRequest.of(page,size);
        }
        GateEntryTransactionPageResponse allGateEntryTransaction = gateEntryTransactionService.getAllGateEntryTransaction(pageable);
        return new ResponseEntity<>(allGateEntryTransaction, HttpStatus.OK);
    }

    /**
     * Save the out time for a gate entry transaction.
     *
     * @param ticketNo The ticket number of the gate entry transaction.
     * @return The status of the operation.
     */
    @PostMapping("/out/{ticketNo}")
    public ResponseEntity<String> saveOutTime(@PathVariable Integer ticketNo) {
        String status = gateEntryTransactionService.setOutTime(ticketNo);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @GetMapping("/transactions")
    public GateEntryTransactionPageResponse getTransactions(
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "5", required = false) int size,
            @RequestParam(required = false, defaultValue = "ticketNo") String sortField,
            @RequestParam(defaultValue = "desc", required = false) String sortOrder,
            @RequestParam(required = false) Integer ticketNo,
            @RequestParam(required = false) String vehicleNo,
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
        GateEntryTransactionPageResponse transactionsByFiltering = gateEntryTransactionService.findTransactionsByFiltering(ticketNo, vehicleNo, date, supplierName,transactionType,pageable);
        return transactionsByFiltering;
    }
}