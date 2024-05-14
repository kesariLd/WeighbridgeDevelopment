package com.weighbridge.SalesManagement.service;

import com.weighbridge.SalesManagement.payloads.SalesDashboardResponse;
import com.weighbridge.SalesManagement.payloads.SalesDetailResponse;
import com.weighbridge.SalesManagement.payloads.SalesOrderRequest;

import java.util.List;

public interface SalesOrderService{

    public String AddSalesDetails(SalesOrderRequest salesOrderRequest);

    public List<SalesDashboardResponse> getAllSalesDetails();

    public SalesDetailResponse getSalesDetails(String purchaseOrderNo);
}