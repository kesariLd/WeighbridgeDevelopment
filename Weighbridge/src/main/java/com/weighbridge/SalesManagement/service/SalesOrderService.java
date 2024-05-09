package com.weighbridge.SalesManagement.service;

import com.weighbridge.SalesManagement.payloads.SalesOrderRequest;

public interface SalesOrderService{

    public String AddSalesDetails(SalesOrderRequest salesOrderRequest);
}