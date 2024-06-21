package com.weighbridge.SalesManagement.controller;

import com.weighbridge.SalesManagement.payloads.*;
import com.weighbridge.SalesManagement.repositories.SalesOrderRespository;
import com.weighbridge.SalesManagement.service.SalesOrderService;
import com.weighbridge.admin.entities.UserMaster;
import com.weighbridge.admin.exceptions.ResourceNotFoundException;
import com.weighbridge.admin.repsitories.UserMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/sales")
public class SalesOrderController {

    @Autowired
    private SalesOrderService salesOrderService;

    @Autowired
    private UserMasterRepository userMasterRepository;

    @PostMapping("/add/salesdetail")
    public ResponseEntity<String> addSalesDetail(@RequestBody SalesOrderRequest salesOrderRequest){
        String str = salesOrderService.AddSalesDetails(salesOrderRequest);
        return ResponseEntity.ok(str);
    }

    @GetMapping("/getAll/sales/{userId}")
    public ResponseEntity<SalesUserPageResponse> getAllSales(@RequestParam(defaultValue = "0", required = false) int page,
                                                                    @RequestParam(defaultValue = "5", required = false) int size,
                                                                    @RequestParam(required = false, defaultValue = "saleOrderNo") String sortField,
                                                                    @RequestParam(defaultValue = "desc", required = false) String sortOrder,@PathVariable String userId){

        Pageable pageable;
        if(sortField!=null && !sortField.isEmpty()){
            Sort.Direction direction = sortOrder.equalsIgnoreCase("desc")?Sort.Direction.DESC:Sort.Direction.ASC;
            Sort sort = Sort.by(direction,sortField);
            pageable = PageRequest.of(page,size,sort);
        }
        else{
            pageable = PageRequest.of(page,size);
        }
        UserMaster byId = userMasterRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("userId doesnt exist"));
        System.out.println(byId);
        SalesUserPageResponse allSalesDetails = salesOrderService.getAllSalesDetails(byId.getCompany().getCompanyId(),byId.getSite().getSiteId(),pageable);
        return ResponseEntity.ok(allSalesDetails);
    }

    @GetMapping("/getSoDetails")
    public ResponseEntity<SalesDetailResponse> getSalesDetail(@RequestParam String saleOrderNo){
        SalesDetailResponse salesDetails = salesOrderService.getSalesDetails(saleOrderNo);
        return ResponseEntity.ok(salesDetails);
    }

    @GetMapping("/getAllVehicleDetails")
    public ResponseEntity<SalesUserPageResponse> getVehiclesDetail(@RequestParam(defaultValue = "0", required = false) int page,
                                                                               @RequestParam(defaultValue = "5", required = false) int size,
                                                                               @RequestParam(required = false, defaultValue = "salePassNo") String sortField,
                                                                               @RequestParam(defaultValue = "desc", required = false) String sortOrder,@RequestParam String userId){
        Pageable pageable;
        if(sortField!=null && !sortField.isEmpty()){
            Sort.Direction direction = sortOrder.equalsIgnoreCase("desc")?Sort.Direction.DESC:Sort.Direction.ASC;
            Sort sort = Sort.by(direction,sortField);
            pageable = PageRequest.of(page,size,sort);
        }
        else{
            pageable = PageRequest.of(page,size);
        }
        SalesUserPageResponse vehiclesAndTransporterDetails = salesOrderService.getVehiclesAndTransporterDetails(pageable,userId);
        return ResponseEntity.ok(vehiclesAndTransporterDetails);
    }


    @GetMapping("/getBySalePassNo")
    public ResponseEntity<VehicleAndTransporterDetail> getVehicleDetailByPassNo(@RequestParam String salePassNo){
        VehicleAndTransporterDetail vehicleAndTransporterDetail=salesOrderService.getBySalePassNo(salePassNo);
        return ResponseEntity.ok(vehicleAndTransporterDetail);
    }

    @GetMapping("/searchBySo")
    public ResponseEntity<SalesDashboardResponse> searchBySaleOrderNo(@RequestParam String saleOrderNo,@RequestParam String userId){
        UserMaster byUserId = userMasterRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("userId doesnt exist"));
        SalesDashboardResponse salesDashboardResponse = salesOrderService.searchBySaleOrderNo(saleOrderNo,byUserId.getSite().getSiteId(),byUserId.getCompany().getCompanyId());
        return ResponseEntity.ok(salesDashboardResponse);
    }
}