package com.weighbridge.weighbridgeoperator.services;

import com.weighbridge.weighbridgeoperator.payloads.WeighbridgeReportResponse;

import java.time.LocalDate;
import java.util.List;

public interface WeighmentReportService {
        List<WeighbridgeReportResponse> generateWeighmentReport(LocalDate startDate, LocalDate endDate);
}
