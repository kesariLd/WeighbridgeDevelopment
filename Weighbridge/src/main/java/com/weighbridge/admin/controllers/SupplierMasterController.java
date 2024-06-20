package com.weighbridge.admin.controllers;


import com.weighbridge.admin.dtos.CustomerMasterDto;
import com.weighbridge.admin.payloads.CustomerRequest;
import com.weighbridge.admin.payloads.SupplierRequest;
import com.weighbridge.admin.services.SupplierMasterService;
import com.weighbridge.admin.dtos.SupplierMasterDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * This class serves as a REST API controller for managing supplier master data.
 * It handles CRUD (Create, Read, Update, Delete) operations on supplier entities
 * through a set of well-defined API endpoints.
 * ("/api/v1/supplier") - This annotation maps all methods of this controller
 * to the base URI "/api/v1/supplier".
 */
@RestController
@RequestMapping("/api/v1/supplier")
public class SupplierMasterController {

    private final SupplierMasterService supplierMasterService;

    /**
     * Constructor to inject the `SupplierMasterService` dependency.
     *
     * @param supplierMasterService - The service class responsible for supplier data access and manipulation logic.
     */
    public SupplierMasterController(SupplierMasterService supplierMasterService) {
        this.supplierMasterService = supplierMasterService;
    }

    /**
     * Creates a new supplier record.
     *
     * @param supplierMasterDto - A DTO object containing the supplier information to be saved.
     *                           The DTO (Data Transfer Object) pattern is often used to decouple the API layer
     *                           from the underlying data model.
     * @return A ResponseEntity object with status code CREATED (201) and the saved supplier DTO upon successful creation,
     *         or an appropriate error response otherwise.
     */
    @PostMapping
    public ResponseEntity<SupplierMasterDto> saveSupplierMaster(@RequestBody SupplierMasterDto supplierMasterDto,@RequestParam String userId) {
        SupplierMasterDto supplier = supplierMasterService.createSupplier(supplierMasterDto,userId);
        return new ResponseEntity<>(supplier, HttpStatus.OK);
    }

    /**
     * Retrieves all supplier records.
     *
     * @return A ResponseEntity object with status code OK (200) containing a list of all supplier DTOs.
     */
    @GetMapping
    public ResponseEntity<List<SupplierMasterDto>> getAllSupplier() {
        List<SupplierMasterDto> allSupplier = supplierMasterService.getAllSupplier();
        return new ResponseEntity<>(allSupplier, HttpStatus.OK);
    }

    /**
     * Retrieves a list of all supplier names as strings.
     *
     * Consider if this endpoint is truly necessary. Returning a list of all supplier DTOs
     * in the `getAllSupplier` method might be sufficient in most cases. If this functionality
     * is required, consider renaming it to something more descriptive, like `getAllSupplierNamesAsString`.
     *
     * @return A ResponseEntity object with status code OK (200) containing a list of supplier names as strings.
     */
    @GetMapping("/get/list")
    public ResponseEntity<List<String>> getAllSupplierAsString() {
        List<String> allSupplierString = supplierMasterService.getAllSupplierAsString();
        return new ResponseEntity<>(allSupplierString, HttpStatus.OK);
    }

    /**
     * Retrieves the address(es) of a supplier given their name.
     *
     * @param supplierName - The name of the supplier to retrieve addresses for.
     *                      This is retrieved from the path variable "{supplierName}".
     * @return A ResponseEntity object with status code OK (200) containing a list of the supplier's addresses.
     *         An empty list is returned if the supplier is not found.
     */
    @GetMapping("/get/{supplierName}")
    public ResponseEntity<List<String>> getSupplierAddressesBySupplierName(@PathVariable String supplierName) {
        List<String> addressOfSupplier = supplierMasterService.getAddressOfSupplier(supplierName);
        return new ResponseEntity<>(addressOfSupplier, HttpStatus.OK);
    }

    @GetMapping("/{supplierName}/addresses")
    public ResponseEntity<List<String>> getSupplierAddressBySupplierName(@PathVariable String supplierName){
        List<String> supplierAddresses = supplierMasterService.getSupplierAddressBySupplierName(supplierName);
        return new ResponseEntity<>(supplierAddresses, HttpStatus.OK);
    }
    @GetMapping("/get/id/{supplierId}")
    public ResponseEntity<SupplierMasterDto> getSupplierDetailsBySupplierId(@PathVariable long supplierId) {
        SupplierMasterDto supplier= supplierMasterService.getSupplierById(supplierId);
        return new ResponseEntity<>(supplier, HttpStatus.OK);
    }

    @PutMapping("/update/{supplierId}")
    public ResponseEntity<String> updateSupplierBySupplierId(@Validated @RequestBody SupplierRequest supplierRequest, @PathVariable long supplierId,@RequestParam String userId){
        String supplierResponse = supplierMasterService.updateSupplierById(supplierRequest, supplierId,userId);
        return new ResponseEntity<>(supplierResponse,HttpStatus.OK);
    }
    @DeleteMapping("/delete/{supplierId}")
    public ResponseEntity<String> deleteSupplierBySupplierId(@PathVariable long supplierId){
        String response = supplierMasterService.deleteSupplierById(supplierId);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PutMapping("/active/{supplierId}")
    public ResponseEntity<String> activeSuppleirBySupplierId(@PathVariable long supplierId){
        String response = supplierMasterService.activeSupplier(supplierId);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}

