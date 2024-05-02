package com.weighbridge.gateuser.payloads;

import lombok.Data;

import java.util.Date;

@Data
public class GateEntryTransactionRequest {

    private String ticketNo;
    private String supplier;
    private String transporter;
    private String material;
    private String vehicle;
    private String site;

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
