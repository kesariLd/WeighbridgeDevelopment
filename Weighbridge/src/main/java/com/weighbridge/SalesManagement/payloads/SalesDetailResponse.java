package com.weighbridge.SalesManagement.payloads;

import lombok.Data;

@Data
public class SalesDetailResponse {
    private String purchaseOrderNo;
    private String productName;
}