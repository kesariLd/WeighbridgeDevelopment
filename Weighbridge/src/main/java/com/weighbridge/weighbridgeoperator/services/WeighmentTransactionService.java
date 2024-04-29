package com.weighbridge.weighbridgeoperator.services;

import com.weighbridge.weighbridgeoperator.payloads.InboundWeighmentRequest;


public interface WeighmentTransactionService {

    public  String inboundWeight(InboundWeighmentRequest weighmentRequest);
}
