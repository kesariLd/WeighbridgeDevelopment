package com.weighbridge.SalesManagement.payloads;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class SalesOrderRequest {

    @NotBlank
    private LocalDate purchaseOrderedDate;
    @NotBlank
    private String purchaseOrderNo;
    @NotBlank
    private String saleOrderNo;

    @NotBlank
    private String customerName;

    @NotBlank
    private String customerAddress;
    @NotBlank
    private String productName;

    private double orderedQuantity;

    private String brokerName;

    private String brokerAddress;
    private String userId;
}