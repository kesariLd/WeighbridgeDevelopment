package com.weighbridge.weighbridgeoperator.controllers;


import com.weighbridge.weighbridgeoperator.payloads.WeighmentRequest;
import com.weighbridge.weighbridgeoperator.payloads.WeighmentTransactionResponse;
import com.weighbridge.weighbridgeoperator.services.WeighmentTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/weighment")
public class WeighmentTransactionController {
    @Autowired
    private WeighmentTransactionService weighmentTransactionService;

    @PostMapping("/measure")
    public ResponseEntity<String> measureWeight(@RequestBody WeighmentRequest weighmentRequest){
        String str = weighmentTransactionService.saveWeight(weighmentRequest);
        return ResponseEntity.ok(str);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<WeighmentTransactionResponse>> getAlldetails(){
        List<WeighmentTransactionResponse> response=weighmentTransactionService.getAllGateDetails();
        return ResponseEntity.ok(response);
    }
}