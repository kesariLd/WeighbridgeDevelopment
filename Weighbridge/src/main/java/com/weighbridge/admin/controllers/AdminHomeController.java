package com.weighbridge.admin.controllers;

import com.weighbridge.admin.services.AdminHomeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/home")
public class AdminHomeController {
    private final AdminHomeService adminHomeService;

    // Constructor injection for AdminService
    public AdminHomeController(AdminHomeService adminHomeService) {
        this.adminHomeService = adminHomeService;
    }

    // End point to find number of all users
    @GetMapping("/all-users")
    public ResponseEntity<Long> findNoOfAllUsers() {
        long noOfAllUsers = adminHomeService.findNoOfAllUsers();
        return ResponseEntity.ok(noOfAllUsers);
    }

    // End point to find number of active users
    @GetMapping("/active-users")
    public ResponseEntity<Long> findNoOfActiveUsers(){
        long noOfActiveUsers = adminHomeService.findNoOfActiveUsers();
        return ResponseEntity.ok(noOfActiveUsers);
    }

    // Endpoint to find number of inactive users
    @GetMapping("/inactive-users")
    public ResponseEntity<Long> findNoOfInActiveUsers(){
        long noOfInActiveUsers = adminHomeService.findNoOfInActiveUsers();
        return ResponseEntity.ok(noOfInActiveUsers);
    }

    // Endpoint to find number of registered vehicles
    @GetMapping("/vehicles")
    public ResponseEntity<Long> findNoOfRegisteredVehicle(){
        long noOfRegisteredVehicle = adminHomeService.findNoOfRegisteredVehicle();
        return ResponseEntity.ok(noOfRegisteredVehicle);
    }

    // Endpoint to find number of registered transporters
    @GetMapping("/transporters")
    public ResponseEntity<Long> findNoOfRegisteredTransporters() {
        long noOfRegisteredTransporters = adminHomeService.findNoOfRegisteredTransporters();
        return ResponseEntity.ok(noOfRegisteredTransporters);
    }

    // Endpoint to find number of registered companies
    @GetMapping("/companies")
    public ResponseEntity<Long> findNoOfRegisteredCompanies() {
        long noOfRegisteredCompanies = adminHomeService.findNoOfRegisteredCompanies();
        return ResponseEntity.ok(noOfRegisteredCompanies);
    }

    // Endpoint to find number of registered suppliers
    @GetMapping("/suppliers")
    public ResponseEntity<Long> findNoOfRegisteredSuppliers() {
        long noOfRegisteredSuppliers = adminHomeService.findNoOfRegisteredSuppliers();
        return ResponseEntity.ok(noOfRegisteredSuppliers);
    }

    // Endpoint to find number of registered customers
    @GetMapping("/customers")
    public ResponseEntity<Long> findNoOfRegisteredCustomers() {
        long noOfRegisteredCustomers = adminHomeService.findNoOfRegisteredCustomers();
        return ResponseEntity.ok(noOfRegisteredCustomers);
    }




}