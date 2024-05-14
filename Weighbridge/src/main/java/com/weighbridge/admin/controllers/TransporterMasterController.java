package com.weighbridge.admin.controllers;

import com.weighbridge.admin.dtos.TransporterDto;
import com.weighbridge.admin.payloads.TransporterRequest;
import com.weighbridge.admin.services.TransporterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * REST controller for managing transporter-related operations.
 */
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

    /**
     * Endpoint for retrieving all transporter names.
     * @return ResponseEntity containing a list of transporter names and HTTP status OK.
     */
    @GetMapping()
    public ResponseEntity<List<String>> getAllTransporterName(){
        List<String> allTransporter = transporterService.getAllTransporterNames();
        return ResponseEntity.ok(allTransporter);
    }

    @GetMapping("/details")
    public ResponseEntity<List<TransporterDto>> getAllTransporter() {
        List<TransporterDto> transporterDtos = transporterService.getAllTransporter();
        return ResponseEntity.ok(transporterDtos);
    }
}