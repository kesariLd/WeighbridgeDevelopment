package com.weighbridge.admin.services;

import com.weighbridge.admin.entities.AcceptableQualityRange;
import com.weighbridge.admin.payloads.QualityRangeRequest;
import com.weighbridge.admin.payloads.QualityRangeResponse;

import java.util.List;

public interface AcceptableQualityRangeService {
    Long createQualityRange(QualityRangeRequest qualityRangeRequest);

    List<QualityRangeResponse> getQualityRangesByMaterial(String materialName);
}
