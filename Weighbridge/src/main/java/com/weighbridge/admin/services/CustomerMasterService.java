package com.weighbridge.admin.services;

import com.weighbridge.admin.dtos.CustomerMasterDto;
import com.weighbridge.admin.payloads.CustomerRequest;

import java.util.List;

public interface CustomerMasterService {
    String createCustomer(CustomerMasterDto customerMasterDto);

    List<CustomerMasterDto> getAllCustomers();

    List<String> getAllCustomerNames();

    List<String> getAddressOfCustomer(String name);
    CustomerMasterDto getCustomerById(long id);
    String updateCustomerById(CustomerRequest customerRequest, long id);

    String deleteCustomerById(long id);
    String activeCustomerId(Long customerId);

}
