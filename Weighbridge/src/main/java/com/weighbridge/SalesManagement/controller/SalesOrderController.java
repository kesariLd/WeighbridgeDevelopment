package com.weighbridge.SalesManagement.controller;

import com.weighbridge.SalesManagement.payloads.SalesDashboardResponse;
import com.weighbridge.SalesManagement.payloads.SalesDetailResponse;
import com.weighbridge.SalesManagement.payloads.SalesOrderRequest;
import com.weighbridge.SalesManagement.payloads.VehicleAndTransporterDetail;
import com.weighbridge.SalesManagement.service.SalesOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
// todo sales person only can have Outbound report
@RestController
@RequestMapping("/api/v1/sales")
public class SalesOrderController {

    @Autowired
    SalesOrderService salesOrderService;

    @PostMapping("/add/salesdetail")
    public ResponseEntity<String> addSalesDetail(@RequestBody SalesOrderRequest salesOrderRequest){
        String str = salesOrderService.AddSalesDetails(salesOrderRequest);
        return ResponseEntity.ok(str);
    }


    @GetMapping("/getAll/sales")
    public ResponseEntity<List<SalesDashboardResponse>> getAllSales(){
        List<SalesDashboardResponse> allSalesDetails = salesOrderService.getAllSalesDetails();
        return ResponseEntity.ok(allSalesDetails);
    }

    @GetMapping("/getPoDetails/{saleOrderNo}")
    public ResponseEntity<SalesDetailResponse> getSalesDetail(@PathVariable String saleOrderNo){
        SalesDetailResponse salesDetails = salesOrderService.getSalesDetails(saleOrderNo);
        return ResponseEntity.ok(salesDetails);
    }

    @GetMapping("/getVehicles/ByPurchasePassNo")
    public ResponseEntity<List<VehicleAndTransporterDetail>> getVehiclesDetail(){
        List<VehicleAndTransporterDetail> vehiclesAndTransporterDetails = salesOrderService.getVehiclesAndTransporterDetails();
        return ResponseEntity.ok(vehiclesAndTransporterDetails);
    }

}