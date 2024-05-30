package com.weighbridge.gateuser.controllers;


import com.weighbridge.gateuser.payloads.GateEntryTransactionPageResponse;
import com.weighbridge.gateuser.payloads.GateEntryTransactionResponse;
import com.weighbridge.gateuser.services.GateEntryTransactionService;
import com.weighbridge.qualityuser.payloads.QualityDashboardResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping("/fetch-InboundTransaction")
    public ResponseEntity<List<GateEntryTransactionResponse>> getInboundTransaction(Pageable pageable) {
        pageable = PageRequest.of(pageable.getPageNumber(), 5, Sort.by("transactionDate").descending());
        GateEntryTransactionPageResponse responses = gateEntryTransactionService.getInboundTransaction(pageable);
        return ResponseEntity.ok(responses.getTransactions());
    }
}

