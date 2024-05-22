package com.weighbridge.qualityuser.payloads;

import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

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
    private String supplierOrCustomerAddress;
    private String transactionType;
    private Map<String, Double> qualityParameters;

//    //for coal
//    private Double moisture;
//    private Double vm;
//    private Double ash;
//    private Double fc;
//    //for iron
//    private Double size;
//    private Double fe_m;
//    private Double fe_t;
//    private Double mtz;
//    private Double carbon;
//    private Double sulphur;
//    private Double non_mag;
//    private Double loi;
}
