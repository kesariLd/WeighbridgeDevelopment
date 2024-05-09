package com.weighbridge.SalesManagement.controller;

import com.weighbridge.SalesManagement.payloads.SalesOrderRequest;
import com.weighbridge.SalesManagement.service.SalesOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sales")
public class SalesOrderController {

    @Autowired
    SalesOrderService salesOrderService;

    @PostMapping("/add/salesdetail")
    public ResponseEntity<String> addSalesDetail(@RequestBody SalesOrderRequest salesOrderRequest){
        String str = salesOrderService.AddSalesDetails(salesOrderRequest);
        return ResponseEntity.ok(str);
    }


}
