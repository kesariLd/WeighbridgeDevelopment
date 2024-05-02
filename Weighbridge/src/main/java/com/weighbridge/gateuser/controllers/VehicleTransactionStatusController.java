package com.weighbridge.gateuser.controllers;

import com.weighbridge.gateuser.services.VehicleTransactionStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/status")
public class VehicleTransactionStatusController {
    @Autowired
    VehicleTransactionStatusService vehicleTransactionStatusService;

    @GetMapping("/pendingGross/Inbound")
    public ResponseEntity<Long> noOfInboundGrossWeight(){
        Long pending = vehicleTransactionStatusService.countInboundPendingAction();
        return ResponseEntity.ok(pending);
    }

    @GetMapping("/pendingTare/Inbound")
    public ResponseEntity<Long> noOfInboundTareWeight(){
        Long pending = vehicleTransactionStatusService.countInboundPendingTare();
        return ResponseEntity.ok(pending);
    }

    @GetMapping("/pendingGross/Outbound")
    public ResponseEntity<Long> noOfOutboundGrossWeight(){
        Long pendingGross = vehicleTransactionStatusService.countOutBoundPendingGross();
        return ResponseEntity.ok(pendingGross);
    }

    @GetMapping("/PendingTare/Outbound")
    public ResponseEntity<Long> noOfOutboundTareWeight(){
        Long pendingTare = vehicleTransactionStatusService.countOutBoundPendingTare();
        return ResponseEntity.ok(pendingTare);
    }
}