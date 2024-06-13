package com.weighbridge.weighbridgeoperator.payloads;

import lombok.Data;

import java.time.LocalDate;
@Data
public class WeighbridgeReportResponseList {
    private Integer ticketNo;
    private String transactionDate;
    private String vehicleNo;
    private String tpNo;
    private LocalDate challanDate;
    private String formattedChallanDate;
    private Double supplyConsignmentWeight;
    private Double weighQuantity;
    private Double excessQty;
}

