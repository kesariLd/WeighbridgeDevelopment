package com.weighbridge.weighbridgeoperator.controllers;

import com.weighbridge.weighbridgeoperator.payloads.WeighmentTransactionResponse;
import com.weighbridge.weighbridgeoperator.services.WeighmentSearchApiService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search/v1/Api")
public class WeighmentSearchApi {

    @Autowired
    private WeighmentSearchApiService weighmentSearchApiService;


    @GetMapping("/searchApi/{ticketNo}")
    public ResponseEntity<WeighmentTransactionResponse> searchByTicketNo(@PathVariable Integer ticketNo){
        WeighmentTransactionResponse byTicketNo = weighmentSearchApiService.getByTicketNo(ticketNo);
        return ResponseEntity.ok(byTicketNo);
    }


    @GetMapping("/serachApi/{fieldName}")
    public ResponseEntity<WeighmentTransactionResponse> searchByVariable(@PathVariable String fieldName){

        return null;
    }
}