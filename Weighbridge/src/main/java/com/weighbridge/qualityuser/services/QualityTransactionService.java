package com.weighbridge.qualityuser.services;

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
    List<QualityDashboardResponse> getAllGateDetails(String userId);

    /**
     * Creates a new quality transaction for a given ticket number.
     *
     * @param ticketNo the ticket number for which the quality transaction is being created
     * @return a string representing the result of the operation
     */
    String createQualityTransaction(Integer ticketNo, String userId, Map<String, Double> transactionRequest);

    /**
     * Retrieves a report response for a given ticket number.
     *
     * @param ticketNo the ticket number for which the report response is being retrieved
     * @return a ReportResponse object containing the report details
     */
    ReportResponse getReportResponse(Integer ticketNo, String userId);

    void passQualityTransaction(Integer ticketNo, String userId);

    List<QualityDashboardResponse> getInboundTransaction(String userId);

    List<QualityDashboardResponse> getOutboundTransaction(String userId);

    List<String> getAllMaterialAndProductNames();

    List<String> getAllProductNames();

    List<String> getAllMaterialNames();

    int getInboundTransactionSize(String userId);

    int getOutboundTransactionSize(String userId);

    int getTotalTransactionSize(String userId);

    List<QualityDashboardResponse> getQCTCompletedInbound(String userId);

    List<QualityDashboardResponse> getQCTCompletedOutbound(String userId);

    List<QualityDashboardResponse> getQCTCompleted(String userId);

    int getInboundQCTCompletedSize(String userId);

    int getOutboundQCTCompletedSize(String userId);

    int getTotalQCTCompletedSize(String userId);


}
