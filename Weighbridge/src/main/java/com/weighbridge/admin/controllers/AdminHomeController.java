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

    public AdminHomeController(AdminHomeService adminHomeService) {
        this.adminHomeService = adminHomeService;
    }

    @GetMapping("/active-users")
    public ResponseEntity<Long> findNoOfActiveUsers(){
        long noOfActiveUsers = adminHomeService.findNoOfActiveUsers();
        return ResponseEntity.ok(noOfActiveUsers);
    }

    @GetMapping("/inactive-users")
    public ResponseEntity<Long> findNoOfInActiveUsers(){
        long noOfInActiveUsers = adminHomeService.findNoOfInActiveUsers();
        return ResponseEntity.ok(noOfInActiveUsers);
    }

    @GetMapping("/vehicles")
    public ResponseEntity<Long> findNoOfRegisteredVehicle(){
        long noOfRegisteredVehicle = adminHomeService.findNoOfRegisteredVehicle();
        return ResponseEntity.ok(noOfRegisteredVehicle);
    }

}