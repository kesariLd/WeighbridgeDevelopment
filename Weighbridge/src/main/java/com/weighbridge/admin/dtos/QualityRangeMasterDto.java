package com.weighbridge.admin.dtos;

import lombok.Data;

@Data
public class QualityRangeMasterDto {
    private long qualityRangeId;
    private String materialName;
    private String parameterName;
    private Double rangeFrom;
    private Double rangeTo;
    private String supplierName;
    private String supplierAddress;
}
