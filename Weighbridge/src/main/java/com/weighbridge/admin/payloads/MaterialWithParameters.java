package com.weighbridge.admin.payloads;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class MaterialWithParameters {
    private String materialName;
    private String materialTypeName;
    @NotBlank(message = "Supplier name is required.")
    private String supplierName;
    @NotBlank(message = "Supplier address is required.")
    private String supplierAddress;
    private List<Parameter> parameters;

    @Data
    public static class Parameter {
        private String parameterName;
        private Double rangeFrom;
        private Double rangeTo;

    }
}
