package com.weighbridge.admin.controllers;

import com.weighbridge.admin.dtos.QualityRangeMasterDto;
import com.weighbridge.admin.entities.QualityRangeMaster;
import com.weighbridge.admin.services.QualityRangeMasterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/quality-parameters")
public class QualityRangeMasterController {

    private final QualityRangeMasterService qualityRangeMasterService;

    public QualityRangeMasterController(QualityRangeMasterService qualityRangeMasterService) {
        this.qualityRangeMasterService = qualityRangeMasterService;
    }

    @GetMapping("/materials")
    private ResponseEntity<List<QualityRangeMasterDto>> getAllQualityRangesForMaterial(){
        List<QualityRangeMasterDto> qualityRangeMasterDtoList = qualityRangeMasterService.getAllQualityRangesForMaterial();
        return ResponseEntity.ok(qualityRangeMasterDtoList);
    }
}
