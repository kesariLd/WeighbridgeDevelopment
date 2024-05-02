package com.weighbridge.admin.payloads;

import lombok.Data;

import java.util.List;

@Data
public class QualityRangeResponse {
    private String materialName;
    private List<ParameterInfo> parameters;

    @Data
    public static class ParameterInfo {
        private String parameterName;
        private Double rangeFrom;
        private Double rangeTo;

    }
}

