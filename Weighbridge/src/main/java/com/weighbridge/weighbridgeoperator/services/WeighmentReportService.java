package com.weighbridge.weighbridgeoperator.services;

import com.weighbridge.weighbridgeoperator.payloads.WeighbridgeReportResponse;
import com.weighbridge.weighbridgeoperator.payloads.WeighmentReportResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface WeighmentReportService {

    Map<String, Map<String, List<WeighmentReportResponse>>> generateWeighmentReport(LocalDate startDate, LocalDate endDate);

    List<WeighbridgeReportResponse> generateWeighmentReportDemo(LocalDate startDate, LocalDate endDate);
}
