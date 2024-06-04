package com.weighbridge.SalesManagement.payloads;

import lombok.Data;

import java.util.List;

@Data
public class SalesUserPageResponse {
    private List<SalesDashboardResponse> sales;
    private Long totalPage;
    private Long totalElement;
}