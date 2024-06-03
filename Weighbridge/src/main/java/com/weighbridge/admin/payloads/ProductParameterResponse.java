package com.weighbridge.admin.payloads;

import lombok.Data;

@Data
public class ProductParameterResponse {
    private String parameterName;
    private Double rangeFrom;
    private Double rangeTo;
    private String productName;
}
