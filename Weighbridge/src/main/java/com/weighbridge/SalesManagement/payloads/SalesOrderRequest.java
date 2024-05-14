package com.weighbridge.SalesManagement.payloads;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;

@Data
public class SalesOrderRequest {

    @NotBlank
    private Date purchaseOrderedDate;
    @NotBlank
    private String purchaseOrderNo;
    @NotBlank
    private String saleOrderNo;

    @NotBlank
    private String customerName;

    @NotBlank
    private String customerAddress;

    private String customerContact;

    private String customerEmail;

    @NotBlank
    private String productName;

    private double orderedQuantity;

    private String brokerName;

    private String brokerAddress;
}