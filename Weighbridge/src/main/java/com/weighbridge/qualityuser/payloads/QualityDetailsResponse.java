package com.weighbridge.qualityuser.payloads;

import com.weighbridge.admin.payloads.QualityRangeResponse;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class QualityDetailsResponse {
    private Integer ticketNo;
    private LocalDate transactionDate;
    private String vehicleNo;
    private LocalDateTime vehicleInTime;
    private LocalDateTime vehicleOutTime;
    private String transporterName;
    private String materialName;
    private String materialTypeName;
    private String tpNo;
    private String poNo;
    private String challanNo;
    private String supplierOrCustomerName;
    private String supplierOrCustomerAddress;
    private String transactionType;
    private List<Parameter> parameters;

    @Data
    public static class Parameter {
        private String parameterName;
        private Double rangeFrom;
        private Double rangeTo;
        private Double parameterValue;

    }
}
