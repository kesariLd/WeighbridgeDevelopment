package com.weighbridge.weighbridgeoperator.services;

import com.weighbridge.weighbridgeoperator.payloads.WeighbridgeOperatorPrint;

public interface WeighbridgeOperatorPrintService {
    public WeighbridgeOperatorPrint getPrintResponse(Integer ticketNo);
}
