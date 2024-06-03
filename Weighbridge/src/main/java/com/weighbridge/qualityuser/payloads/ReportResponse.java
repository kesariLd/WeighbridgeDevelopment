package com.weighbridge.qualityuser.payloads;

import lombok.Data;

import java.util.Map;

@Data
public class ReportResponse {
    private Integer ticketNo;
    private String companyName;
    private String companyAddress;
    private String date;
    private String vehicleNo;
    private String materialOrProduct;
    private String materialTypeOrProductType;
    private String supplierOrCustomerName;
    private String supplierOrCustomerAddress;
    private String transactionType;
    private Map<String, Double> qualityParameters;

    private Boolean QualityParametersPresent;
}
