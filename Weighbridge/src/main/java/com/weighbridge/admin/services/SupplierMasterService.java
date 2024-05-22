package com.weighbridge.admin.services;

import com.weighbridge.admin.dtos.SupplierMasterDto;
import com.weighbridge.admin.dtos.SupplierMasterDto;
import com.weighbridge.admin.payloads.SupplierRequest;

import java.util.List;

public interface SupplierMasterService {

    SupplierMasterDto createSupplier(SupplierMasterDto supplierMasterDto);

    List<SupplierMasterDto> getAllSupplier();
    List<String> getAllSupplierAsString();

    List<String> getAddressOfSupplier(String supplierName);

    List<String> getSupplierAddressBySupplierName(String supplierName);

    SupplierMasterDto getSupplierById(long id);
    String updateSupplierById(SupplierRequest SupplierRequest, long id);
}
