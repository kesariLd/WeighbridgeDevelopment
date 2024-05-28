package com.weighbridge.weighbridgeoperator.payloads;

import lombok.Data;

import java.util.List;

@Data
public class WeighbridgePageResponse {

    private List<WeighmentTransactionResponse> weighmentTransactionResponses;
    private Long totalPages;
    private Long totalElements;
}
