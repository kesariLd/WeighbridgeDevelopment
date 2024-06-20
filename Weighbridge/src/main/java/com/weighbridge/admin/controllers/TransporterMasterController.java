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
    public ResponseEntity<String> addTransporters(@RequestBody TransporterRequest transporterRequest,@RequestParam String userId){
        String response = transporterService.addTransporter(transporterRequest,userId);
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

    /**
     * Endpoint to retrieve all the transporters.
     *
     * @return ResponseEntity containing  alist of transporters dtos and HTTP status OK.
     */
    @GetMapping("/details")
    public ResponseEntity<List<TransporterDto>> getAllTransporter() {
        List<TransporterDto> transporterDtos = transporterService.getAllTransporter();
        return ResponseEntity.ok(transporterDtos);
    }

    /**
     * Endpoint to retrieve a transporter by its ID.
     *
     * @param transporterId The unique identifier of the transporter.
     * @return ResponseEntity containing the details with HTTP status OK.
     */
    @GetMapping("/{transporterId}")
    public ResponseEntity<TransporterDto> getTransporterById(@PathVariable Long transporterId) {
        TransporterDto transporterDto = transporterService.getTransporterById(transporterId);
        return ResponseEntity.ok(transporterDto);
    }

    /**
     * Endpoint to update a transporter by its ID.
     *
     * @param transporterId  The unique identifier of the transporter.
     * @param transporterDto The transporter details to be updated.
     * @return ResponseEntity containing a success message with HTTP status OK.
     */
    @PutMapping("/{transporterId}")
    public ResponseEntity<String> updateTransporterById(@PathVariable Long transporterId, @RequestBody TransporterDto transporterDto,@RequestParam String userId) {
        String response = transporterService.updateTransporterById(transporterId, transporterDto,userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{transporterId}/deactivate")
    public ResponseEntity<Void> deleteTransporterById(@PathVariable Long transporterId) {
        boolean deactivated = transporterService.deactivateTransporterById(transporterId);
        if (deactivated) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{transporterId}/activate")
    public ResponseEntity<Void> activateTransporterById(@PathVariable Long transporterId) {
        boolean activated = transporterService.activateTransporterById(transporterId);
        if (activated) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}