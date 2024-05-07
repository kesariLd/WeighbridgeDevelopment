package com.weighbridge.weighbridgeoperator.payloads;

import lombok.Data;

@Data
public class TicketResponse {
    private String tpN0;
    private String poNo;
    private String challanNo;
    private String vehicleNo;
    private String supplierName;
    private String supplierAddress;
    private String transporter;
    private String driverDlNo;
    private String driverName;
    private String material;
}
