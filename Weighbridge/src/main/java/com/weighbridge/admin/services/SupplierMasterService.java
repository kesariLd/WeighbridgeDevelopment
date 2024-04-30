package com.weighbridge.admin.services;

import com.weighbridge.admin.dtos.SupplierMasterDto;

import java.util.List;

public interface SupplierMasterService {

    SupplierMasterDto createSupplier(SupplierMasterDto supplierMasterDto);

    List<SupplierMasterDto> getAllSupplier();
    List<String> getAllSupplierAsString();

    List<String> getAddressOfSupplier(String supplierName);
}
