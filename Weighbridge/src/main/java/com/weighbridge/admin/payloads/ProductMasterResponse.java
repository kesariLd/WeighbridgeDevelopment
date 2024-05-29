package com.weighbridge.admin.payloads;

import lombok.Data;

@Data
public class ProductMasterResponse {
    private long id;
    private String productName;
    private String productTypeName;
    private String parameterName;
    private Double rangeFrom;
    private Double rangeTo;
}
