package com.weighbridge.admin.payloads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class MaterialWithParameters {
    @NotBlank(message = "Material is requried")
    private String materialName;

    //    private String materialTypeName;
    @NotBlank(message = "Supplier name is required.")
    private String supplierName;

    @NotBlank(message = "Supplier address is required.")
    private String supplierAddress;

    @NotNull(message = "Parameter are required")
    private List<Parameter> parameters;

    @Data
    public static class Parameter {
        private String parameterName;
        private Double rangeFrom;
        private Double rangeTo;

    }
}
