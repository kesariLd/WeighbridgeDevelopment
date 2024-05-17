package com.weighbridge.SalesManagement.payloads;

import lombok.Data;

@Data
public class VehicleAndTransporterDetail {
    private String salePassNo;
    private String vehicleNo;
    private String saleOrderNo;
    private String purchaseOrderNo;
    private String customerName;
    private String customerAddress;
    private String transporterName;
    private String productName;
    private String productType;
    private double consignmentWeight;
}