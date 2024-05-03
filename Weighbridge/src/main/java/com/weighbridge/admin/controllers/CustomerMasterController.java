package com.weighbridge.admin.controllers;


import com.weighbridge.admin.dtos.CustomerMasterDto;
import com.weighbridge.admin.dtos.SupplierMasterDto;
import com.weighbridge.admin.services.CustomerMasterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class for managing operations related to customers data.
 */
@RestController
@RequestMapping("/api/v1/customers")
public class CustomerMasterController {


    private final CustomerMasterService customerMasterService;

    public CustomerMasterController(CustomerMasterService customerMasterService) {
        this.customerMasterService = customerMasterService;
    }


    @PostMapping
    public ResponseEntity<String> createCustomer(@RequestBody CustomerMasterDto customerMasterDto) {
        String response = customerMasterService.createSupplier(customerMasterDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CustomerMasterDto>> getAllCustomers() {
        List<CustomerMasterDto> allCustomer = customerMasterService.getAllCustomers();
        return ResponseEntity.ok(allCustomer);
    }

    @GetMapping("/names")
    public ResponseEntity<List<String>> getAllCustomerNames() {
        List<String> allCustomerNames = customerMasterService.getAllCustomerNames();
        return ResponseEntity.ok(allCustomerNames);
    }

}
