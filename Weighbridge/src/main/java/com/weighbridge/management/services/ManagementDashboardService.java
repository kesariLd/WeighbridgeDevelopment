package com.weighbridge.management.services;

import com.weighbridge.management.payload.ManagementGateEntryList;
import com.weighbridge.management.payload.ManagementPayload;
import com.weighbridge.management.payload.MaterialProductDataResponse;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ManagementDashboardService {
    MaterialProductDataResponse getMaterialProductBarChartData(ManagementPayload managementRequest);
    List<Map<String, Object>> managementGateEntryDashboard(ManagementPayload managementRequest);

    ManagementGateEntryList gateEntryList(Integer ticketNo, String vehicleNo, LocalDate date, String supplierName, String transactionType, Pageable pageable, String vehicleStatus,String company,String site);
}
