package com.weighbridge.admin.controllers;


import com.weighbridge.admin.services.SupplierMasterService;
import com.weighbridge.admin.dtos.SupplierMasterDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * Controller class for managing operations related to supplier master data.
 */
@RestController
@RequestMapping("/api/v1/supplier")
public class SupplierMasterController {

    @Autowired
    private SupplierMasterService supplierMasterService;

    /**
     * Saves a new supplier.
     * @param supplierMasterDto The DTO containing information about the supplier to be saved.
     * @return ResponseEntity containing the saved supplier DTO and HTTP status OK.
     */
    @PostMapping
    public ResponseEntity<SupplierMasterDto> saveSupplierMaster(@RequestBody SupplierMasterDto supplierMasterDto){
        SupplierMasterDto supplier = supplierMasterService.createSupplier(supplierMasterDto);
        return new ResponseEntity<>(supplier, HttpStatus.OK);
    }

    /**
     * Retrieves all suppliers.
     * @return ResponseEntity containing a list of all supplier DTOs and HTTP status OK.
     */
    @GetMapping
    public ResponseEntity<List<SupplierMasterDto>> getAllSupplier(){
        List<SupplierMasterDto> allSupplier = supplierMasterService.getAllSupplier();
        return new ResponseEntity<>(allSupplier, HttpStatus.OK);
    }

    /**
     * Retrieves all suppliers as strings.
     * @return ResponseEntity containing a list of all supplier names as strings and HTTP status OK.
     */
    @GetMapping("/get/list")
    public ResponseEntity<List<String>> getAllSupplierAsString(){
        List<String> allSupplierString = supplierMasterService.getAllSupplierAsString();
        return new ResponseEntity<>(allSupplierString, HttpStatus.OK);
    }

    /**
     * Retrieves the address of a supplier by its name.
     * @param supplierName The name of the supplier.
     * @return ResponseEntity containing a list of supplier addresses and HTTP status OK.
     */
    @GetMapping("/get/{supplierName}")
    public ResponseEntity<List<String>> getSupplierAddressBySupplierName(@PathVariable String supplierName){
        List<String> addressOfSupplier = supplierMasterService.getAddressOfSupplier(supplierName);
        return new ResponseEntity<>(addressOfSupplier, HttpStatus.OK);
    }
}
