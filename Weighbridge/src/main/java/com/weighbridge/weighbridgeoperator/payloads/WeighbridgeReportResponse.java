package com.weighbridge.weighbridgeoperator.payloads;

import lombok.Data;

import java.util.List;

@Data
public class WeighbridgeReportResponse {
    private String materialName;
    private String supplierOrCustomer;
    private List<WeighbridgeReportResponse2> weighbridgeResponse2List;
}
