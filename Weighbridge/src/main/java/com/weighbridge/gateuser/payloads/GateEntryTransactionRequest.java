package com.weighbridge.gateuser.payloads;

import lombok.Data;

import java.util.Date;

/**
 * GateEntryTransactionRequest class for take the request data from GateEntryTransactionRequest Form from frontend
 */
@Data
public class GateEntryTransactionRequest {

    private String ticketNo;
    private String supplier;
    private String supplierAddressLine1;
    private String customer;
    private String customerAddressLine;
    private String transporter;
    private String material;
    private String vehicle;
    private String site;
    private String transactionType;
    private String company;
    private String dlNo;
    private String driverName;
    private Double supplyConsignmentWeight;
    private String poNo;
    private String tpNo;
    private String challanNo;
    private String ewayBillNo;
    private String tranasactionType;
}
