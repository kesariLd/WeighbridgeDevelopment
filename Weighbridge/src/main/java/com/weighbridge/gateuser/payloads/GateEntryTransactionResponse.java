package com.weighbridge.gateuser.payloads;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * GateEntryTransactionResponse class for to return necessary details to frontend
 */
@Data
public class GateEntryTransactionResponse {

    private Integer ticketNo;
    private String vehicleNo;
    private LocalDateTime vehicleIn;
    private LocalDateTime vehicleOut;
    private String vehicleType;
    private Integer vehicleWheelsNo;
    private String transporter;
    private String supplier;
    private String supplierAddress;
    private String material;
    private Double tpNetWeight;
    private String poNo;
    private String tpNo;
    private String challanNo;
    private String transactionType;
}
