package com.weighbridge.weighbridgeoperator.payloads;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TicketResponse {
    private String tpNo;
    private String poNo;
    private String challanNo;
    private String vehicleNo;
    private String supplierName;
    private String supplierAddress;
    private String transporter;
    private String driverDlNo;
    private String driverName;
    private String material;
    private double grossWeight;
    private double tareWeight;
    private double netWeight;
    private LocalDateTime grossWeightTime;
    private LocalDateTime tareWeightTime;
    private double consignmentWeight;
}