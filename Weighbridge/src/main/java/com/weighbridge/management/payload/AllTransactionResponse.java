package com.weighbridge.management.payload;

import lombok.Data;

@Data
public class AllTransactionResponse {
    private Long noOfGateEntry;
    private Long noOfGateExit;
    private Long noOfTareWeight;
    private Long noOfGrossWeight;
    private Long noOfQualityTransaction;
}
