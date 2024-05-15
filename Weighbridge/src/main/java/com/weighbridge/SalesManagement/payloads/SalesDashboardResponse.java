package com.weighbridge.SalesManagement.payloads;

import lombok.Data;

@Data
public class SalesDashboardResponse {
  private String saleOrderNo;
  private String purchaseOrderNo;
  private String customerName;
  private String productName;
  private String brokerName;
  private double orderedQty;
  private double progressiveQty;
  private double balanceQty;
}