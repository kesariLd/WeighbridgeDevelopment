package com.weighbridge.qualityuser.controller;

import com.weighbridge.admin.services.MaterialMasterService;
import com.weighbridge.admin.services.ProductMasterService;
import com.weighbridge.qualityuser.payloads.QualityDashboardResponse;
import com.weighbridge.qualityuser.services.QualityTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<List<String>> getProductsOrMaterials(){
        List<String> allMaterialAndProductNames = qualityTransactionService.getAllMaterialAndProductNames();
        return ResponseEntity.ok(allMaterialAndProductNames);
    }

    @GetMapping("fetch-InboundTransaction")
    public ResponseEntity<List<QualityDashboardResponse>> getInboundTransaction(){
        List<QualityDashboardResponse> responses=qualityTransactionService.getInboundTransaction();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("fetch-OutboundTransaction")
    public  ResponseEntity<List<QualityDashboardResponse>>getOutboundTransaction(){
        List<QualityDashboardResponse> responses=qualityTransactionService.getOutboundTransaction();
        return ResponseEntity.ok(responses);
    }
}
