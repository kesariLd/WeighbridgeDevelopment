package com.weighbridge.management.payload;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CoalMoisturePercentageResponse {
    private String materialName;
    private String supplierName;
    private String supplierAddress;
    private List<MoisturePercentageData> moisturePercentageData;

    @Data
    public static class MoisturePercentageData {
        private LocalDate transactionDate;
        private String parameterName;
        private double moisturePercentage;
    }
}
