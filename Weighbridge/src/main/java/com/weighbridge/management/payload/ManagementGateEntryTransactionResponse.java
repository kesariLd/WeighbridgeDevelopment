package com.weighbridge.management.payload;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ManagementGateEntryTransactionResponse {

    private Integer ticketNo;
    private String vehicleNo;
    private String vehicleIn;
    private String vehicleOut;
    private String vehicleType;
    private Integer vehicleWheelsNo;
    private String transporter;
    private String supplier;
    private String supplierAddress;
    private String customer;
    private String customerAddress;
    private String material;
    private String materialType;
    private Double tpNetWeight;
    private String poNo;
    private String tpNo;
    private String challanNo;
    private LocalDate transactionDate;
    private LocalDate challanDate;
    private String transactionType;
    private String currentStatus;
    private double grossWeight;
    private double tareWeight;
    private double netWeight;
    private Integer weighmentNo;
    //Required to enable and disable quality report button
    private Boolean quality;
}