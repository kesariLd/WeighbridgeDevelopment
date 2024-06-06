package com.weighbridge.management.services;

import com.weighbridge.management.payload.ManagementPayload;
import com.weighbridge.management.payload.MaterialProductDataResponse;
import com.weighbridge.management.payload.MaterialProductQualityResponse;

import java.util.List;
import java.util.Map;

public interface ManagementDashboardService {
    MaterialProductDataResponse getMaterialProductBarChartData(ManagementPayload managementRequest);

    MaterialProductQualityResponse getMaterialProductQualities(ManagementPayload managementRequest);

    List<Map<String, Object>> managementGateEntryDashboard(ManagementPayload managementRequest);

}
