package com.weighbridge.weighbridgeoperator.services;

import com.weighbridge.weighbridgeoperator.payloads.WeighmentPrintResponse;
import com.weighbridge.weighbridgeoperator.payloads.WeighbridgeReportResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Interface defining a service for generating weighment reports.
 *
 * This service provides methods to retrieve weighment data and format it
 * into reports based on optional start and end dates.
 */
public interface WeighmentReportService {


    WeighmentPrintResponse getAllWeighmentTransactions(Integer ticketNo,String userId);
  

        /**
         * Generates a weighment report based on optional start and end dates.
         *
         * This method retrieves weighment data from the underlying storage system
         * and formats it into a list of {@link WeighbridgeReportResponse} objects
         * representing the report data.
         *
         * @param startDate The starting date for the report (format: YYYY-MM-DD), optional.
         *                  If not provided, all weighment data is returned.
         * @param endDate The ending date for the report (format: YYYY-MM-DD), optional.
         * @param startDate endDate is both null than a response will come , Date is not provided
         * @return A list of {@link WeighbridgeReportResponse} objects containing
         *         the weighment report data.
         */
        List<WeighbridgeReportResponse> generateWeighmentReport(LocalDate startDate, LocalDate endDate, String companyName, String siteName, String userId);

    List<Map<String, Object>> generateCustomizedReport(List<String> selectedFields,LocalDate startDate,LocalDate endDate,String userId);

}
