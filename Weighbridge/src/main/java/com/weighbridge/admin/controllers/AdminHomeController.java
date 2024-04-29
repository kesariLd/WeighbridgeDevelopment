package com.weighbridge.admin.controllers;

import com.weighbridge.admin.services.AdminHomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/home")
public class AdminHomeController {

    @Autowired
    private AdminHomeService adminHomeService;

    @GetMapping("/activeUsers")
    public ResponseEntity<Long> findNoOfActiveUsers(){
        long noOfActiveUsers = adminHomeService.findNoOfActiveUsers();
        return ResponseEntity.ok(noOfActiveUsers);
    }


    @GetMapping("/inActiveUsers")
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