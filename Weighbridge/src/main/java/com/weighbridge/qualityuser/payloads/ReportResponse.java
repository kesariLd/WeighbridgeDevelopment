package com.weighbridge.qualityuser.payloads;

import lombok.Data;

import java.time.LocalDate;
@Data
public class ReportResponse {
    private Integer ticketNo;
    private String companyName;
    private String companyAddress;
    private LocalDate date;
    private String vehicleNo;
    private String materialOrProduct;
    private String materialTypeOrProductType;
    private String supplierOrCustomerName;
    private String transactionType;

    private double moisture;
    private double vm;
    private double ash;
    private double fc;
    private double size_20mm;
    private double size_03mm;
    private double fe_t;
    private double loi;
}
