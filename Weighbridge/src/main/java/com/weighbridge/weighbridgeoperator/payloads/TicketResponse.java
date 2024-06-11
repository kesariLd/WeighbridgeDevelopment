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
    private String customerName;
    private String customerAdress;
    private String transporter;
    private String driverDlNo;
    private String driverName;
    private String material;
    private double grossWeight;
    private double tareWeight;
    private double netWeight;

    private double consignmentWeight;

    private String grossWeightTime;
    private String tareWeightTime;

}