package com.weighbridge.admin.payloads;

import lombok.Data;

@Data
public class QualityRangeRequest {
    private String materialName;
    private String parameterName;
    private double minValue;
    private double maxValue;
}
