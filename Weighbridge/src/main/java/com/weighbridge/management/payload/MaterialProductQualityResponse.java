package com.weighbridge.management.payload;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class MaterialProductQualityResponse {
    private String companyName;
    private String siteName;
    private List<MaterialProductQualityData> materialProductQualityData;

    @Data
    public static class MaterialProductQualityData {
        private LocalDate transactionDate;
        List<QualityData> qualityData;
    }

    @Data
    public static class QualityData {
        private String materialOrProductName;
        private Double goodPercentage;
        private Double badPercentage;
    }
}
