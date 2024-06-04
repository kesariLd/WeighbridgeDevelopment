package com.weighbridge.management.services;

import com.weighbridge.management.payload.ManagementPayload;
import com.weighbridge.management.payload.MaterialProductDataResponse;

public interface ManagementDashboardService {
    MaterialProductDataResponse getMaterialProductBarChartData(ManagementPayload managementRequest);
}
