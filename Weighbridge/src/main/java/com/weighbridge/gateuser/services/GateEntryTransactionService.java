package com.weighbridge.gateuser.services;

import com.weighbridge.gateuser.dtos.GateEntryTransactionDto;
import com.weighbridge.gateuser.entities.GateEntryTransaction;
import com.weighbridge.gateuser.payloads.GateEntryTransactionPageResponse;
import com.weighbridge.gateuser.payloads.GateEntryTransactionRequest;
import com.weighbridge.gateuser.payloads.GateEntryTransactionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface for managing gate entry transactions.
 */
public interface GateEntryTransactionService {

    /**
     * Saves a gate entry transaction based on the provided request.
     *
     * @param gateEntryTransactionRequest The request containing the details of the gate entry transaction.
     * @return The ticket number of the saved gate entry transaction.
     */
    Integer saveGateEntryTransaction(GateEntryTransactionRequest gateEntryTransactionRequest);

    /**
     * Sets the out time for a vehicle associated with the specified ticket number.
     *
     * @param ticketNo The ticket number of the vehicle for which out time is to be set.
     * @return A message indicating whether the vehicle can exit or not.
     */
    String setOutTime(Integer ticketNo);

    /**
     * Retrieves all gate entry transactions associated with the current user session.
     *
     * @return A list of gate entry transaction responses containing various details such as ticket number,
     *         transaction type, material, supplier, vehicle information, timestamps, and other related data.
     */
    GateEntryTransactionPageResponse getAllGateEntryTransaction(Pageable pageable);

    List<GateEntryTransactionResponse> getAllGateEntryTransactionForWeighmentReport(LocalDate startDate , LocalDate endDate);
    GateEntryTransactionPageResponse findTransactionsByFiltering(Integer ticketNo, String vehicleNo, LocalDate date,String supplierName,String transactionType,Pageable pageable);
//    List<Object[]> getDemoData(List<String> selectedFields);
}

