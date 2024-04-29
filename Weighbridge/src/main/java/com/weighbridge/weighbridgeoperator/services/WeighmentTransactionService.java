package com.weighbridge.weighbridgeoperator.services;


import com.weighbridge.weighbridgeoperator.payloads.WeighmentRequest;


public interface WeighmentTransactionService {

    String saveWeight(WeighmentRequest weighmentRequest);
}
