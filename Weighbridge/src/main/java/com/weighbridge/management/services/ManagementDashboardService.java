package com.weighbridge.management.services;

import com.weighbridge.management.dtos.WeightResponseForGraph;
import com.weighbridge.management.payload.ManagementPayload;
import com.weighbridge.management.payload.MaterialProductDataResponse;


import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ManagementDashboardService {
    MaterialProductDataResponse getMaterialProductBarChartData(ManagementPayload managementRequest);

    List<WeightResponseForGraph> getQtyResponseInGraph(ManagementPayload managementPayload);
    List<Map<String, Object>> managementGateEntryDashboard(ManagementPayload managementRequest);

}
