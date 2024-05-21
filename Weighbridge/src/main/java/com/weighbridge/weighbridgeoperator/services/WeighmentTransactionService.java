package com.weighbridge.weighbridgeoperator.services;


import com.weighbridge.weighbridgeoperator.payloads.TicketResponse;
import com.weighbridge.weighbridgeoperator.payloads.WeighmentRequest;
import com.weighbridge.weighbridgeoperator.payloads.WeighmentTransactionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


/**
 * Interface for managing weighment transactions.
 */
public interface WeighmentTransactionService {

    /**
     * Saves the weight based on the provided weighment request.
     *
     * @param weighmentRequest The request containing weighment details.
     * @return A string indicating the status of the save operation.
     */
    String saveWeight(WeighmentRequest weighmentRequest);

    /**
     * Retrieves all gate details.
     *
     * @return A list of WeighmentTransactionResponse containing all gate details.
     */
  List<WeighmentTransactionResponse> getAllGateDetails(Pageable pageable);

    /**
     * Retrieves the ticket response by ticket number.
     *
     * @param ticketNo The ticket number.
     * @return The TicketResponse corresponding to the provided ticket number.
     */
    TicketResponse getResponseByTicket(Integer ticketNo);
}

