package com.weighbridge.admin.payloads;

import lombok.Data;

@Data
public class MaterialParameterResponse {
    private String parameterName;
    private Double rangeFrom;
    private Double rangeTo;
    private String supplierName;
    private String supplierAddress;
    private String materialName;
}
