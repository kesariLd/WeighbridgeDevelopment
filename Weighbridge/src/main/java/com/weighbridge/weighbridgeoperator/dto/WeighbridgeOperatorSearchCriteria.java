package com.weighbridge.weighbridgeoperator.dto;



import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDate;


@Data
public class WeighbridgeOperatorSearchCriteria {
    private Integer ticketNo;
    private String transactionType;
    private LocalDate transactionDate;
    private String vehicleNo;
    private String supplierName;
    private String customerName;
    private String transporterName;
    private String productName;
    private String materialName;
    private String userId;
    private String companyId;
    private String siteId;
    private Boolean today; // Add this field
}