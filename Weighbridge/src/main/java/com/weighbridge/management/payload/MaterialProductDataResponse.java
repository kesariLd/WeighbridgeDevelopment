package com.weighbridge.management.payload;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
public class MaterialProductDataResponse {
    private String companyName;
    private String siteName;
    private List<MaterialProductData> materialProductData;

    @Data
    public static class MaterialProductData {
        private LocalDate transactionDate;
        private Map<String, Double> materialData;
    }
}
