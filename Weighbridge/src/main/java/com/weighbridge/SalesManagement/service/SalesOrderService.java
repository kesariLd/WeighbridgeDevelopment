package com.weighbridge.SalesManagement.service;

import com.weighbridge.SalesManagement.payloads.SalesDashboardResponse;
import com.weighbridge.SalesManagement.payloads.SalesDetailResponse;
import com.weighbridge.SalesManagement.payloads.SalesOrderRequest;
import com.weighbridge.SalesManagement.payloads.VehicleAndTransporterDetail;

import java.util.List;

public interface SalesOrderService{

    public String AddSalesDetails(SalesOrderRequest salesOrderRequest);

    public List<SalesDashboardResponse> getAllSalesDetails();

    public SalesDetailResponse getSalesDetails(String purchaseOrderNo);

    public List<VehicleAndTransporterDetail> getVehiclesAndTransporterDetails();

    public VehicleAndTransporterDetail getBySalePassNo(String salePassNo);
}