package com.weighbridge.SalesManagement.controller;

import com.weighbridge.SalesManagement.payloads.SalesProcessRequest;
import com.weighbridge.SalesManagement.service.SalesProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/salesProcess")
public class SalesProcessController {
    @Autowired
    SalesProcessService salesProcessService;

    @PostMapping("/salesProcess")
    public ResponseEntity<String> addSalesProcess(@RequestBody SalesProcessRequest salesProcessRequest){
        String sales = salesProcessService.addSalesProcess(salesProcessRequest);
        return ResponseEntity.ok(sales);
    }
}
