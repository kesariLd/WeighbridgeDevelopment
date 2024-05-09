package com.weighbridge.qualityuser.services;

import com.weighbridge.qualityuser.payloads.QualityDetailsResponse;
import com.weighbridge.qualityuser.payloads.QualityRequest;
import com.weighbridge.qualityuser.payloads.QualityResponse;
import com.weighbridge.qualityuser.payloads.ReportResponse;

import java.util.List;

public interface QualityTransactionService {
    List<QualityResponse> getAllGateDetails();

    String createQualityTransaction(Integer ticketNo, QualityRequest qualityTransaction);

    ReportResponse getReportResponse(Integer ticketNo);

    byte[] generateQualityReport(ReportResponse reportResponse);
  
    QualityDetailsResponse getDetailsForQualityTransaction(Integer ticketNo);

}
