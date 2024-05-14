package com.weighbridge.weighbridgeoperator.payloads;


import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class WeighmentReportResponse {
    private String materialName;
    private String supplier;
    private LocalDate transactionDate;
    private String vehicleNo;
    private String tpNo;
    private LocalDate challanDate;
    private Double supplyConsignmentWeight;
    private Double weighQuantity;
    private Double totalQuantity;
}
