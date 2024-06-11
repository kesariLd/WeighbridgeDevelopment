package com.weighbridge.admin.services;

import com.weighbridge.admin.dtos.QualityRangeMasterDto;

import java.util.List;

public interface QualityRangeMasterService {
    List<QualityRangeMasterDto> getAllQualityRangesForMaterial();
}
