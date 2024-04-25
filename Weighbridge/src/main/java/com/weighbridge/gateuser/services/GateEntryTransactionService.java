package com.weighbridge.gateuser.services;

import com.weighbridge.gateuser.dtos.GateEntryTransactionDto;
import com.weighbridge.gateuser.entities.GateEntryTransaction;
import com.weighbridge.gateuser.payloads.GateEntryTransactionRequest;
import com.weighbridge.gateuser.payloads.GateEntryTransactionResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface GateEntryTransactionService {

    GateEntryTransaction saveGateEntryTransaction(GateEntryTransactionRequest gateEntryTransactionRequest);
    List<GateEntryTransaction> getAllGateEntryTraansaction();

    String setOutTime(Integer ticketNo);


    List<GateEntryTransactionResponse> getAllGateEntryTransaction();
}
