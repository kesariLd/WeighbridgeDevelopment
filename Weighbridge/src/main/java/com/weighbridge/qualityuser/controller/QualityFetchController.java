package com.weighbridge.qualityuser.controller;

import com.weighbridge.qualityuser.payloads.QualityDashboardResponse;
import com.weighbridge.qualityuser.services.QualityTransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/qualities")
public class QualityFetchController {

    private final QualityTransactionService qualityTransactionService;

    public QualityFetchController(QualityTransactionService qualityTransactionService) {
        this.qualityTransactionService = qualityTransactionService;
    }

    @GetMapping("fetch-ProductsOrMaterials")
    public ResponseEntity<List<String>> getProductsOrMaterials() {
        List<String> allMaterialAndProductNames = qualityTransactionService.getAllMaterialAndProductNames();
        return ResponseEntity.ok(allMaterialAndProductNames);
    }

    @GetMapping("/products")
    public ResponseEntity<List<String>> getProducts() {
        List<String> productNames = qualityTransactionService.getAllProductNames();
        return ResponseEntity.ok(productNames);
    }

    @GetMapping("/materials")
    public ResponseEntity<List<String>> getMaterials() {
        List<String> materialNames = qualityTransactionService.getAllMaterialNames();
        return ResponseEntity.ok(materialNames);
    }

    @GetMapping("fetch-InboundTransaction")
    public ResponseEntity<List<QualityDashboardResponse>> getInboundTransaction(@RequestParam String userId) {
        List<QualityDashboardResponse> responses = qualityTransactionService.getInboundTransaction(userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("fetch-OutboundTransaction")
    public ResponseEntity<List<QualityDashboardResponse>> getOutboundTransaction(String userId) {
        List<QualityDashboardResponse> responses = qualityTransactionService.getOutboundTransaction(userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/inbound/pending")
    public ResponseEntity<Integer> getInboundTransactionSize(@RequestParam String userId) {
        int size = qualityTransactionService.getInboundTransactionSize(userId);
        return ResponseEntity.ok(size);
    }

    @GetMapping("/outbound/pending")
    public ResponseEntity<Integer> getOutboundTransactionSize(@RequestParam String userId) {
        int size = qualityTransactionService.getOutboundTransactionSize(userId);
        return ResponseEntity.ok(size);
    }

    @GetMapping("/total/pending")
    public ResponseEntity<Integer> getTotalTransactionSize(@RequestParam String userId) {
        int size = qualityTransactionService.getTotalTransactionSize(userId);
        return ResponseEntity.ok(size);
    }

    @GetMapping("/inbound-qct-completed-size")
    public ResponseEntity<Integer> getInboundQCTCompletedSize(@RequestParam String userId) {
        int size = qualityTransactionService.getInboundQCTCompletedSize(userId);
        return ResponseEntity.ok(size);
    }

    @GetMapping("/outbound-qct-completed-size")
    public ResponseEntity<Integer> getOutboundQCTCompletedSize(@RequestParam String userId) {
        int size = qualityTransactionService.getOutboundQCTCompletedSize(userId);
        return ResponseEntity.ok(size);
    }

    @GetMapping("/total-qct-completed-size")
    public ResponseEntity<Integer> getTotalQCTCompletedSize(@RequestParam String userId) {
        int totalSize = qualityTransactionService.getTotalQCTCompletedSize(userId);
        return ResponseEntity.ok(totalSize);
    }
}
