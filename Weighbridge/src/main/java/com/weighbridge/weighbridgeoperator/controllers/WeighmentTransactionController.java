package com.weighbridge.weighbridgeoperator.controllers;


import com.weighbridge.weighbridgeoperator.payloads.*;
import com.weighbridge.weighbridgeoperator.services.WeighbridgeOperatorPrintService;
import com.weighbridge.weighbridgeoperator.services.WeighmentTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/weighment")
public class WeighmentTransactionController {
    @Autowired
    private WeighmentTransactionService weighmentTransactionService;

    @Autowired
    private WeighbridgeOperatorPrintService weighbridgeOperatorPrintService;

    @PostMapping("/measure")
    public ResponseEntity<String> measureWeight(@RequestBody WeighmentRequest weighmentRequest){
        String str = weighmentTransactionService.saveWeight(weighmentRequest);
        return ResponseEntity.ok(str);
    }

    @GetMapping("/getAll")
    public ResponseEntity<WeighbridgePageResponse> getAlldetails( @RequestParam(defaultValue = "0", required = false) int page,
                                                                             @RequestParam(defaultValue = "5", required = false) int size,
                                                                             @RequestParam(required = false, defaultValue = "ticketNo") String sortField,
                                                                             @RequestParam(defaultValue = "desc", required = false) String sortOrder){

        Pageable pageable;
        if(sortField!=null && !sortField.isEmpty()){
            Sort.Direction direction = sortOrder.equalsIgnoreCase("desc")?Sort.Direction.DESC:Sort.Direction.ASC;
            Sort sort = Sort.by(direction,sortField);
            pageable = PageRequest.of(page,size,sort);
        }
        else{
            pageable = PageRequest.of(page,size);
        }

      WeighbridgePageResponse response=weighmentTransactionService.getAllGateDetails(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get/{ticketNo}")
    public ResponseEntity<TicketResponse> getResponseByTicket(@PathVariable Integer ticketNo){
        TicketResponse responseByTicket = weighmentTransactionService.getResponseByTicket(ticketNo);
        return ResponseEntity.ok(responseByTicket);
    }

    @GetMapping("/getPrintTicketWise/{ticketNo}")
    public ResponseEntity<WeighbridgeOperatorPrint> getPrintResponse(@PathVariable Integer ticketNo){
        WeighbridgeOperatorPrint printResponse = weighbridgeOperatorPrintService.getPrintResponse(ticketNo);
        return ResponseEntity.ok(printResponse);
    }

    @GetMapping("/getCompletedTransaction")
    public ResponseEntity<WeighbridgePageResponse> getCompletedTransactions(@RequestParam(defaultValue = "0", required = false) int page,
                                                                            @RequestParam(defaultValue = "10", required = false) int size,
                                                                            @RequestParam(required = false, defaultValue = "gateEntryTransaction") String sortField,
                                                                            @RequestParam(defaultValue = "desc", required = false) String sortOrder){
        Pageable pageable;
        if(sortField!=null && !sortField.isEmpty()){
            Sort.Direction direction = sortOrder.equalsIgnoreCase("desc")?Sort.Direction.DESC:Sort.Direction.ASC;
            Sort sort = Sort.by(direction,sortField);
            pageable = PageRequest.of(page,size,sort);
        }
        else{
            pageable = PageRequest.of(page,size);
        }
        WeighbridgePageResponse allCompletedTickets = weighmentTransactionService.getAllCompletedTickets(pageable);
        return ResponseEntity.ok(allCompletedTickets);
    }

}