package com.weighbridge.gateuser.payloads;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * GateEntryTransactionResponse class for to return necessary details to frontend
 */
@Data
public class GateEntryTransactionResponse {

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

    //Required to enable and disable quality report button
    private Boolean quality;
}
