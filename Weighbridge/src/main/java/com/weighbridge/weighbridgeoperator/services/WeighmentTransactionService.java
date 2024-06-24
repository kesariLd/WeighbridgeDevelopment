package com.weighbridge.weighbridgeoperator.services;


import com.weighbridge.weighbridgeoperator.payloads.TicketResponse;
import com.weighbridge.weighbridgeoperator.payloads.WeighbridgePageResponse;
import com.weighbridge.weighbridgeoperator.payloads.WeighmentRequest;
import com.weighbridge.weighbridgeoperator.payloads.WeighmentTransactionResponse;
import com.weighbridge.weighbridgeoperator.services.impls.TicketImageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

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
    String saveWeight(WeighmentRequest weighmentRequest, String userId, MultipartFile frontImg1, MultipartFile backImg2, MultipartFile topImg3,
                      MultipartFile bottomImg4, MultipartFile leftImg5,
                      MultipartFile rightImg6, String role);

    /**
     * Retrieves all gate details.
     *
     * @return A list of WeighmentTransactionResponse containing all gate details.
     */
  WeighbridgePageResponse getAllGateDetails(Pageable pageable,String userId);

    /**
     * Retrieves the ticket response by ticket number.
     *
     * @param ticketNo The ticket number.
     * @return The TicketResponse corresponding to the provided ticket number.
     */
    TicketResponse getResponseByTicket(Integer ticketNo);

    WeighbridgePageResponse getAllCompletedTickets(Pageable pageable,String userId);
    TicketImageResponse viewResponseByTicket(Integer ticketNo, String userId);
}