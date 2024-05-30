package com.weighbridge.SalesManagement.payloads;

import lombok.Data;

@Data
public class SalesDetailResponse {
    private String saleOrderNo;
    private String productName;
    private double balanceWeight;
}