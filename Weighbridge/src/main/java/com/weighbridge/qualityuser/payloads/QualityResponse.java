package com.weighbridge.qualityuser.payloads;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Data
public class QualityResponse {

    private Integer ticketNo;
    private LocalDate date;
    private String vehicleNo;
    private LocalDateTime in;
    private LocalDateTime out;
    private String transporterName;
    private String material;
    private String materialType;
    private String tpNo;
    private String poNo;
    private String challanNo;
    private String supplierOrCustomerName;
    private String supplierOrCustomerAddress;
    private String transactionType;




}
