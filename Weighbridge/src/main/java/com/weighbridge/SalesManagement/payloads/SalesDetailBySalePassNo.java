package com.weighbridge.SalesManagement.payloads;

import lombok.Data;

@Data
public class SalesDetailBySalePassNo {
    private String salePassNo;
    private double consignmentWeight;
    private String productName;
    private String productType;
    private String transporterName;
    private String vehicleNo;
}
