package com.weighbridge.management.services;

import com.weighbridge.management.payload.CoalMoisturePercentageRequest;
import com.weighbridge.management.payload.CoalMoisturePercentageResponse;
import com.weighbridge.management.payload.ManagementGateEntryList;
import com.weighbridge.management.payload.ManagementPayload;
import com.weighbridge.management.payload.MaterialProductDataResponse;
import org.springframework.data.domain.Pageable;


import com.weighbridge.management.dtos.WeightResponseForGraph;
import com.weighbridge.management.payload.AllTransactionResponse;
import com.weighbridge.management.payload.ManagementPayload;
import com.weighbridge.management.payload.ManagementQualityDashboardResponse;
import com.weighbridge.management.payload.MaterialProductDataResponse;
import com.weighbridge.management.payload.MaterialProductQualityResponse;



import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ManagementDashboardService {
    MaterialProductDataResponse getMaterialProductBarChartData(ManagementPayload managementRequest);

    CoalMoisturePercentageResponse getMoisturePercentage(CoalMoisturePercentageRequest coalMoisturePercentageRequest);

    List<WeightResponseForGraph> getQtyResponseInGraph(ManagementPayload managementPayload,String transactionType);

    MaterialProductQualityResponse getMaterialProductQualities(ManagementPayload managementRequest);

    List<Map<String, Object>> managementGateEntryDashboard(ManagementPayload managementRequest);


    List<ManagementQualityDashboardResponse> getGoodOrBadQualities(ManagementPayload managementRequest, String transactionType, String qualityType);

    AllTransactionResponse getAllTransactionResponse(ManagementPayload managementPayload,String transactionType);


    ManagementGateEntryList gateEntryList(Integer ticketNo, String vehicleNo, LocalDate date, String supplierName, String transactionType, Pageable pageable, String vehicleStatus,String company,String site);


    List<ManagementQualityDashboardResponse> getGoodQualities(ManagementPayload managementRequest, String transactionType);

    List<ManagementQualityDashboardResponse> getBadQualities(ManagementPayload managementRequest, String transactionType);

    ManagementQualityDashboardResponse searchByTicketNo(String ticketNo, String companyName, String siteName);
}

