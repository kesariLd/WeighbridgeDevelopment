package com.weighbridge.qualityuser.services;

import com.weighbridge.qualityuser.exception.ResourceNotFoundException;
import com.weighbridge.qualityuser.payloads.QualityDashboardResponse;

import java.util.List;

public interface QualityTransactionSearchService {

    List<QualityDashboardResponse> searchByDate(String date);

    QualityDashboardResponse searchByTicketNo(Integer ticketNo ,boolean checkQualityCompleted) throws ResourceNotFoundException;

    List<QualityDashboardResponse> searchByVehicleNo(String vehicleNo);

    List<QualityDashboardResponse> searchBySupplierOrCustomerNameAndAddress(String supplierOrCustomerName, String supplierOrCustomerAddress);

}
