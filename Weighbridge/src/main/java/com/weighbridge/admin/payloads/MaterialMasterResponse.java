package com.weighbridge.admin.payloads;

import lombok.Data;

@Data
public class MaterialMasterResponse {
    private long id;
    private String materialName;
    private String materialTypeName;
    private String supplierName;
    private String supplierAddress;
    private String parameterName;
    private Double rangeFrom;
    private Double rangeTo;
}
