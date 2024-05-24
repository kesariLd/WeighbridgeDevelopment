package com.weighbridge.qualityuser.services;

import com.weighbridge.qualityuser.payloads.QualityCreationResponse;
import com.weighbridge.qualityuser.payloads.QualityDashboardPaginationResponse;
import com.weighbridge.qualityuser.payloads.QualityDashboardResponse;
import com.weighbridge.qualityuser.payloads.ReportResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    QualityDashboardPaginationResponse getAllGateDetails(Pageable pageable);

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


    List<QualityDashboardResponse> searchByTicketNoVehicleNoSupplierAndSupplierAddress(Integer ticketNo, String vehicleNo, String supplierOrCustomerName, String supplierOrCustomerAddress);

    List<QualityDashboardResponse> searchByDate(String date);


    void passQualityTransaction(Integer ticketNo);

}
