package com.weighbridge.admin.controllers;

import com.weighbridge.admin.entities.AcceptableQualityRange;
import com.weighbridge.admin.services.AcceptableQualityRangeService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/quality-range")
public class AcceptableQualityRangeMasterController {

    private final AcceptableQualityRangeService acceptableQualityRangeService;

    public AcceptableQualityRangeMasterController(AcceptableQualityRangeService acceptableQualityRangeService) {
        this.acceptableQualityRangeService = acceptableQualityRangeService;
    }
}
