package com.weighbridge.SalesManagement.service;

import com.weighbridge.SalesManagement.payloads.SalesDetailBySalePassNo;
import com.weighbridge.SalesManagement.payloads.SalesDetailResponse;
import com.weighbridge.SalesManagement.payloads.SalesProcessRequest;

import java.util.List;

public interface SalesProcessService {

    public String addSalesProcess(SalesProcessRequest request);

    public List<SalesDetailBySalePassNo> getBySalePassNo(String saleOrderNo);
}
