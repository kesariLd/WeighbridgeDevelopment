package com.weighbridge.admin.controllers;

import com.weighbridge.admin.entities.AcceptableQualityRange;
import com.weighbridge.admin.payloads.QualityRangeRequest;
import com.weighbridge.admin.payloads.QualityRangeResponse;
import com.weighbridge.admin.services.AcceptableQualityRangeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/quality-ranges")
public class AcceptableQualityRangeMasterController {

    private final AcceptableQualityRangeService acceptableQualityRangeService;

    public AcceptableQualityRangeMasterController(AcceptableQualityRangeService acceptableQualityRangeService) {
        this.acceptableQualityRangeService = acceptableQualityRangeService;
    }

    @PostMapping
    public ResponseEntity<Long> createQualityRange(@RequestBody QualityRangeRequest qualityRangeRequest) {
        Long qualityRangeId = acceptableQualityRangeService.createQualityRange(qualityRangeRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(qualityRangeId);
    }

    @GetMapping("/materials/{materialName}")
    public ResponseEntity<List<QualityRangeResponse>> getQualityRangesByMaterial(@PathVariable String materialName) {
        List<QualityRangeResponse> acceptableQualityRange = acceptableQualityRangeService.getQualityRangesByMaterial(materialName);
        return ResponseEntity.status(HttpStatus.CREATED).body(acceptableQualityRange);
    }
}
