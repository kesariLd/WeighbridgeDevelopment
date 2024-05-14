package com.weighbridge.SalesManagement.payloads;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;

@Data
public class SalesProcessRequest {
    @NotBlank
    private String purchaseOrderNo;
    private String productName;
    private String productType;
    @NotBlank
    private String vehicleNo;
    @NotBlank
    private String transporterName;

    @NotBlank
    private Date purchaseProcessDate;
}