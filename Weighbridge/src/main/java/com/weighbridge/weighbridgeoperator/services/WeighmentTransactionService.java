package com.weighbridge.weighbridgeoperator.services;


import com.weighbridge.weighbridgeoperator.payloads.TicketResponse;
import com.weighbridge.weighbridgeoperator.payloads.WeighmentRequest;
import com.weighbridge.weighbridgeoperator.payloads.WeighmentTransactionResponse;

import java.util.List;


public interface WeighmentTransactionService {

    String saveWeight(WeighmentRequest weighmentRequest);

    List<WeighmentTransactionResponse> getAllGateDetails();

    TicketResponse getResponseByTicket(Integer ticketNo);

}
