package com.weighbridge.weighbridgeoperator.services;

import com.weighbridge.weighbridgeoperator.payloads.WeighmentTransactionResponse;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface WeighmentSearchApiService {
    WeighmentTransactionResponse getByTicketNo(Integer ticketNo);

    List<WeighmentTransactionResponse> getBySearchfield(String fieldName);
}
