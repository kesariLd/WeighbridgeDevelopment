package com.weighbridge.qualityuser.payloads;

import lombok.Data;

@Data
public class QualityRequest {
    private double moisture;
    private double vm;
    private double ash;
    private double fc;
    private double size_20mm;
    private double size_03mm;
    private double fe_t;
    private double loi;
}
