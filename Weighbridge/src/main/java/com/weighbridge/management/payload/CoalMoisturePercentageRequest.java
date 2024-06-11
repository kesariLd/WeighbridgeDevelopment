package com.weighbridge.management.payload;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CoalMoisturePercentageRequest {
    private String companyName;
    private String siteName;
    private String materialName;
    private String supplierName;
    private String supplierAddress;
    private LocalDate fromDate;
    private LocalDate toDate;
}
