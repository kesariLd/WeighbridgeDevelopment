package com.weighbridge.gateuser.dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class GateEntryPrint {
    private String companyName;
    private String siteName;
    private Integer ticketNo;
    private String vehicleNo;
    private String vehicleIn;
    private String vehicleOut;
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
    private String transactionDate;
    private String challanDate;
    private String transactionType;
    private String productName;
    private String productType;
}