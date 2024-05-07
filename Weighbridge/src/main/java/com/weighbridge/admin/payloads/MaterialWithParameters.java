package com.weighbridge.admin.payloads;

import lombok.Data;

import java.util.List;

@Data
public class MaterialWithParameters {
    private String materialName;
    private String materialTypeName;
    private List<Parameter> parameters;

    @Data
    public static class Parameter {
        private String parameterName;
        private Double rangeFrom;
        private Double rangeTo;

    }
}
