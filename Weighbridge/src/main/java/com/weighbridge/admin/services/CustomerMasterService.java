package com.weighbridge.admin.services;

import com.weighbridge.admin.dtos.CustomerMasterDto;

import java.util.List;

public interface CustomerMasterService {
    String createSupplier(CustomerMasterDto customerMasterDto);

    List<CustomerMasterDto> getAllCustomers();

    List<String> getAllCustomerNames();

    List<String> getAddressOfCustomer(String name);

}
