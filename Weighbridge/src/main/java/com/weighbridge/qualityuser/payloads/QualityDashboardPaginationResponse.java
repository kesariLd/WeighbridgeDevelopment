package com.weighbridge.qualityuser.payloads;

import lombok.Data;

import java.util.List;

@Data
public class QualityDashboardPaginationResponse {
    private List<QualityDashboardResponse> qualityDashboardResponseList;
    private Integer totalPages;
    private  long totalElements;

}
