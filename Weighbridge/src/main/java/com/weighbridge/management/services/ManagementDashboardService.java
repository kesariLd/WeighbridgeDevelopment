package com.weighbridge.management.services;

import com.weighbridge.management.payload.ManagementPayload;
import com.weighbridge.management.payload.MaterialProductDataResponse;
import com.weighbridge.management.payload.MaterialProductQualityResponse;

public interface ManagementDashboardService {
    MaterialProductDataResponse getMaterialProductBarChartData(ManagementPayload managementRequest);

    MaterialProductQualityResponse getMaterialProductQualities(ManagementPayload managementRequest);
}
