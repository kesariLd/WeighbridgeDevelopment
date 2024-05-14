package com.weighbridge.weighbridgeoperator.services;

import com.weighbridge.weighbridgeoperator.payloads.WeighmentReportResponse;

import java.util.List;
import java.util.Map;

public interface WeighmentReportService {

    public Map<String, Map<String, List<WeighmentReportResponse>>> generateWeighmentReport();
}
