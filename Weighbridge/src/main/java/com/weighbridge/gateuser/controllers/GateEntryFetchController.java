package com.weighbridge.gateuser.controllers;


import com.weighbridge.gateuser.services.GateEntryTransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/gate")
public class GateEntryFetchController {
    private final GateEntryTransactionService gateEntryTransactionService;

    public GateEntryFetchController(GateEntryTransactionService gateEntryTransactionService) {
        this.gateEntryTransactionService = gateEntryTransactionService;
    }

    @GetMapping("fetch-ProductsOrMaterials")
    public ResponseEntity<List<String>> getProductsOrMaterials(){
        List<String> allMaterialAndProductNames = gateEntryTransactionService.getAllMaterialAndProductNames();
        return ResponseEntity.ok(allMaterialAndProductNames);
    }
}

