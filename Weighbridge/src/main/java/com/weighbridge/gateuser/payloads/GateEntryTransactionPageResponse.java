package com.weighbridge.gateuser.payloads;

import lombok.Data;

import java.util.List;
@Data
public class GateEntryTransactionPageResponse {
    private List<GateEntryTransactionResponse> transactions;
    private int totalPages;
}
