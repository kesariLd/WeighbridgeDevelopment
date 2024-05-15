package com.weighbridge.weighbridgeoperator.services;

import com.weighbridge.weighbridgeoperator.payloads.WeighmentPrintResponse;
import com.weighbridge.weighbridgeoperator.payloads.WeighbridgeReportResponse;
import java.time.LocalDate;
import java.util.List;

public interface WeighmentReportService {

    Map<String, Map<String, List<WeighmentReportResponse>>> generateWeighmentReport(LocalDate startDate, LocalDate endDate);

    WeighmentPrintResponse getAllWeighmentTransactions(Integer ticketNo);
  
    List<WeighbridgeReportResponse> generateWeighmentReport(LocalDate startDate, LocalDate endDate);
}
