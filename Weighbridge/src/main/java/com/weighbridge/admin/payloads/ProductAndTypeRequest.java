package com.weighbridge.admin.payloads;

import lombok.Data;

@Data
public class ProductAndTypeRequest {
    private String productName;
    private String productTypeName;
}
