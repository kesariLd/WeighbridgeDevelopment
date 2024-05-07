package com.weighbridge.admin.controllers;


import com.weighbridge.admin.dtos.CustomerMasterDto;
import com.weighbridge.admin.dtos.SupplierMasterDto;
import com.weighbridge.admin.services.CustomerMasterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * This class serves as a REST API controller for managing customer data.
 * It handles CRUD (Create, Read, Update, Delete) operations on customer entities
 * through a set of well-defined API endpoints.
 * "/api/v1/customers" - This annotation maps all the methods of this controller
 * to the base URI "/api/v1/customers".
 */
@RestController
@RequestMapping("/api/v1/customers")
public class CustomerMasterController {

    private final CustomerMasterService customerMasterService;

    /**
     * Constructor to inject the `CustomerMasterService` dependency.
     *
     * @param customerMasterService - The service class responsible for customer data access and manipulation logic.
     */
    public CustomerMasterController(CustomerMasterService customerMasterService) {
        this.customerMasterService = customerMasterService;
    }

    /**
     * Creates a new customer record.
     *
     * @param customerMasterDto - A DTO object containing the customer data to be created.
     *                           The DTO (Data Transfer Object) pattern is often used to decouple the API layer
     *                           from the underlying data model.
     * @return A ResponseEntity object with status code CREATED (201) and a success message
     *         upon successful creation, or an appropriate error response otherwise.
     * @throws Exception - If any unexpected error occurs during customer creation.
     */
    @PostMapping
    public ResponseEntity<String> createCustomer(@RequestBody CustomerMasterDto customerMasterDto) throws Exception {
        String response = customerMasterService.createSupplier(customerMasterDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves all customer records.
     *
     * @return A ResponseEntity object with status code OK (200) containing a list of all customer DTOs.
     * @throws Exception - If any unexpected error occurs during customer retrieval.
     */
    @GetMapping
    public ResponseEntity<List<CustomerMasterDto>> getAllCustomers() throws Exception {
        List<CustomerMasterDto> allCustomer = customerMasterService.getAllCustomers();
        return ResponseEntity.ok(allCustomer);
    }

    /**
     * Retrieves a list of all customer names.
     *
     * @return A ResponseEntity object with status code OK (200) containing a list of all customer names.
     * @throws Exception - If any unexpected error occurs during customer name retrieval.
     */
    @GetMapping("/names")
    public ResponseEntity<List<String>> getAllCustomerNames() throws Exception {
        List<String> allCustomerNames = customerMasterService.getAllCustomerNames();
        return ResponseEntity.ok(allCustomerNames);
    }

    /**
     * Retrieves the address(es) of a customer given their name.
     *
     * @param customerName - The name of the customer to retrieve addresses for.
     *                      This is retrieved from the path variable "{customerName}".
     * @return A ResponseEntity object with status code OK (200) containing a list of the customer's addresses.
     *         An empty list is returned if the customer is not found.
     * @throws Exception - If any unexpected error occurs during customer address retrieval.
     */
    @GetMapping("/get/{customerName}")
    public ResponseEntity<List<String>> getCustsomerAddressByCustomerName(@PathVariable String customerName) throws Exception {
        List<String> addressOfSupplier = customerMasterService.getAddressOfCustomer(customerName);
        return new ResponseEntity<>(addressOfSupplier, HttpStatus.OK);
    }
}
