package com.weighbridge.qualityuser.services;

import com.weighbridge.qualityuser.exception.ResourceNotFoundException;
import com.weighbridge.qualityuser.payloads.QualityCreationResponse;
import com.weighbridge.qualityuser.payloads.QualityDashboardResponse;
import com.weighbridge.qualityuser.payloads.ReportResponse;

import java.util.List;
import java.util.Map;

/**
 * This interface provides methods for managing quality transactions.
 */
public interface QualityTransactionService {

    /**
     * Retrieves a list of all gate details.
     *
     * @return a list of QualityDashboardResponse objects
     */
    List<QualityDashboardResponse> getAllGateDetails();

    /**
     * Creates a new quality transaction for a given ticket number.
     *
     * @param ticketNo the ticket number for which the quality transaction is being created
     * @return a string representing the result of the operation
     */
    String createQualityTransaction(Integer ticketNo, Map<String, Double> transactionRequest);

    /**
     * Retrieves a report response for a given ticket number.
     *
     * @param ticketNo the ticket number for which the report response is being retrieved
     * @return a ReportResponse object containing the report details
     */
    ReportResponse getReportResponse(Integer ticketNo);

    void passQualityTransaction(Integer ticketNo);

    List<QualityDashboardResponse> getInboundTransaction();

    List<QualityDashboardResponse> getOutboundTransaction();

    List<QualityDashboardResponse> getQCTCompleted();

    List<String> getAllMaterialAndProductNames();
}
