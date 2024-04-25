package com.weighbridge.admin.controllers;

import com.weighbridge.admin.payloads.TransporterRequest;
import com.weighbridge.admin.services.TransporterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transporter")
public class TransporterMasterController {

    @Autowired
    private TransporterService transporterService;

    /**
     * Endpoint for saving transporter.
     * @param transporterRequest
     * @return ResponseEntity with a success message containing transporter added successfully.
     */
    @PostMapping()
    public ResponseEntity<String> addTransporters(@RequestBody TransporterRequest transporterRequest){
        String response = transporterService.addTransporter(transporterRequest);
        return ResponseEntity.ok(response);
    }


    @GetMapping()
    public ResponseEntity<List<String>> getAllTransporterName(){
        List<String> allTransporter = transporterService.getAllTransporter();
        return ResponseEntity.ok(allTransporter);
    }
}