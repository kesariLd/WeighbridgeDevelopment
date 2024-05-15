package com.weighbridge.qualityuser.services;

import com.weighbridge.qualityuser.payloads.QualityCreationResponse;
import com.weighbridge.qualityuser.payloads.QualityRequest;
import com.weighbridge.qualityuser.payloads.QualityDashboardResponse;
import com.weighbridge.qualityuser.payloads.ReportResponse;

import java.util.List;

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
     * @param qualityTransaction the QualityRequest object containing the quality transaction details
     * @return a string representing the result of the operation
     */
    String createQualityTransaction(Integer ticketNo, QualityRequest qualityTransaction);

    /**
     * Retrieves a report response for a given ticket number.
     *
     * @param ticketNo the ticket number for which the report response is being retrieved
     * @return a ReportResponse object containing the report details
     */
    ReportResponse getReportResponse(Integer ticketNo);

    /**
     * Generates a quality report based on the provided report response.
     *
     * @param reportResponse the ReportResponse object containing the report details
     * @return a byte array representing the generated quality report
     */
//    byte[] generateQualityReport(ReportResponse reportResponse);

    /**
     * Retrieves details for a quality transaction based on the given ticket number.
     *
     * @param ticketNo the ticket number for which the quality transaction details are being retrieved
     * @return a QualityCreationResponse object containing the quality transaction details
     */
    QualityCreationResponse getDetailsForQualityTransaction(Integer ticketNo);
}
