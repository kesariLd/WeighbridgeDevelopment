package com.weighbridge.management.payload;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ManagementQualityDashboardResponse {

    private Integer ticketNo;
    private String vehicleNo;
    private String productOrMaterialName;
    private String productOrMaterialType;
    private String supplierOrCustomerName;
    private String supplierOrCustomerAddress;
    private String transactionType;
    private String qualityType;
    private String transactionDate;
}
