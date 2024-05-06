package com.weighbridge.qualityuser.services;

import com.weighbridge.qualityuser.payloads.QualityResponse;
import com.weighbridge.weighbridgeoperator.payloads.WeighmentTransactionResponse;

import java.util.List;

public interface QualityTransactionService {
     List<QualityResponse> getAllGateDetails();


}
