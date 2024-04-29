package com.weighbridge.weighbridgeoperator.controllers;

import com.weighbridge.weighbridgeoperator.payloads.InboundWeighmentRequest;

import com.weighbridge.weighbridgeoperator.services.WeighmentTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/inbound/weighment")
public class WeighmentTransactionController {
    @Autowired
    private WeighmentTransactionService weighmentTransactionService;

    @PostMapping("/measure")
    public ResponseEntity<String> measureWeight(@RequestBody InboundWeighmentRequest weighmentRequest){
        String str = weighmentTransactionService.inboundWeight(weighmentRequest);
        return ResponseEntity.ok(str);
    }

}
