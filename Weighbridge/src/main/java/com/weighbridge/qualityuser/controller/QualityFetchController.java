package com.weighbridge.qualityuser.controller;

import com.weighbridge.admin.services.MaterialMasterService;
import com.weighbridge.admin.services.ProductMasterService;
import com.weighbridge.qualityuser.payloads.QualityDashboardResponse;
import com.weighbridge.qualityuser.services.QualityTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/qualities")
public class QualityFetchController {

    private final QualityTransactionService qualityTransactionService;
    @Autowired
    private final ProductMasterService productMasterService;
    @Autowired
    private final MaterialMasterService materialMasterService;


    public QualityFetchController(QualityTransactionService qualityTransactionService, ProductMasterService productMasterService, MaterialMasterService materialMasterService) {
        this.qualityTransactionService = qualityTransactionService;
        this.productMasterService = productMasterService;
        this.materialMasterService = materialMasterService;
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
    public ResponseEntity<List<QualityDashboardResponse>> getInboundTransaction() {
        List<QualityDashboardResponse> responses = qualityTransactionService.getInboundTransaction();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("fetch-OutboundTransaction")
    public ResponseEntity<List<QualityDashboardResponse>> getOutboundTransaction() {
        List<QualityDashboardResponse> responses = qualityTransactionService.getOutboundTransaction();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/inbound/pending")
    public ResponseEntity<Integer> getInboundTransactionSize() {
        int size = qualityTransactionService.getInboundTransactionSize();
        return ResponseEntity.ok(size);
    }

    @GetMapping("/outbound/pending")
    public ResponseEntity<Integer> getOutboundTransactionSize() {
        int size = qualityTransactionService.getOutboundTransactionSize();
        return ResponseEntity.ok(size);
    }

    @GetMapping("/total/pending")
    public ResponseEntity<Integer> getTotalTransactionSize() {
        int size = qualityTransactionService.getTotalTransactionSize();
        return ResponseEntity.ok(size);
    }

    @GetMapping("/inbound-qct-completed-size")
    public ResponseEntity<Integer> getInboundQCTCompletedSize() {
        int size = qualityTransactionService.getInboundQCTCompletedSize();
        return ResponseEntity.ok(size);
    }

    @GetMapping("/outbound-qct-completed-size")
    public ResponseEntity<Integer> getOutboundQCTCompletedSize() {
        int size = qualityTransactionService.getOutboundQCTCompletedSize();
        return ResponseEntity.ok(size);
    }

    @GetMapping("/total-qct-completed-size")
    public ResponseEntity<Integer> getTotalQCTCompletedSize() {
        int totalSize = qualityTransactionService.getTotalQCTCompletedSize();
        return ResponseEntity.ok(totalSize);
    }
}
