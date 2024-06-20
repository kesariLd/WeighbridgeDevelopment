package com.weighbridge.admin.controllers;

import com.weighbridge.admin.dtos.CustomerMasterDto;
import com.weighbridge.admin.payloads.CustomerRequest;
import com.weighbridge.admin.services.CustomerMasterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
     */
    @PostMapping
    public ResponseEntity<String> createCustomer(@RequestBody CustomerMasterDto customerMasterDto, @RequestParam String userId) {
        String response = customerMasterService.createCustomer(customerMasterDto,userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves all customer records.
     *
     * @return A ResponseEntity object with status code OK (200) containing a list of all customer DTOs.
     */
    @GetMapping
    public ResponseEntity<List<CustomerMasterDto>> getAllCustomers() {
        List<CustomerMasterDto> allCustomer = customerMasterService.getAllCustomers();
        return ResponseEntity.ok(allCustomer);
    }

    /**
     * Retrieves a list of all customer names.
     *
     * @return A ResponseEntity object with status code OK (200) containing a list of all customer names.
     */
    @GetMapping("/names")
    public ResponseEntity<List<String>> getAllCustomerNames() {
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
     */
    @GetMapping("/get/{customerName}")
    public ResponseEntity<List<String>> getCustsomerAddressByCustomerName(@PathVariable String customerName) {
        List<String> addressOfSupplier = customerMasterService.getAddressOfCustomer(customerName);
        return new ResponseEntity<>(addressOfSupplier, HttpStatus.OK);
    }
    @GetMapping("/get/id/{customerId}")
    public ResponseEntity<CustomerMasterDto> getCustsomerDetailsByCustomerId(@PathVariable long customerId) {
        CustomerMasterDto customer= customerMasterService.getCustomerById(customerId);
        return new ResponseEntity<>(customer, HttpStatus.OK);
    }
    
    @PutMapping("/update/{customerId}")
    public ResponseEntity<String> updateCustomerByCustomerId(@Validated @RequestBody CustomerRequest customerRequest, @PathVariable long customerId,@RequestParam String userId){
        String customerResponse = customerMasterService.updateCustomerById(customerRequest, customerId,userId);
        return new ResponseEntity<>(customerResponse,HttpStatus.OK);
    }

    @DeleteMapping("/delete/{customerId}")
    public ResponseEntity<String> deleteCustomerByCustomerId(@PathVariable long customerId){
        String response = customerMasterService.deleteCustomerById(customerId);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    @PutMapping("/active/{customerId}")
    public ResponseEntity<String> activeCustomerByCustomerId(@PathVariable long customerId){
        String response = customerMasterService.activeCustomerId(customerId);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
