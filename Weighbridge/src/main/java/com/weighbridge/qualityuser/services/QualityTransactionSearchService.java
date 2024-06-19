package com.weighbridge.qualityuser.services;

import com.weighbridge.qualityuser.exception.ResourceNotFoundException;
import com.weighbridge.qualityuser.payloads.QualityDashboardResponse;

import java.util.List;

public interface QualityTransactionSearchService {

    List<QualityDashboardResponse> searchByDate(String date, String userId);

    QualityDashboardResponse searchByTicketNo(Integer ticketNo, String userId, boolean checkQualityCompleted);

    List<QualityDashboardResponse> searchByVehicleNo(String vehicleNo, String userId);

    List<QualityDashboardResponse> searchBySupplierOrCustomerNameAndAddress(String supplierOrCustomerName, String supplierOrCustomerAddress, String userId);

    List<QualityDashboardResponse> searchByQCTCompletedVehicleNo(String vehicleNo, String userId);

    List<QualityDashboardResponse> searchBySupplierOrCustomerNameAndAddressQctCompleted(String supplierOrCustomerName, String supplierOrCustomerAddress, String userId);
}
