package com.weighbridge.weighbridgeoperator.controllers;

import com.weighbridge.weighbridgeoperator.services.VehicleTransactionStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API controller for managing vehicle transaction statuses.
 *
 * This class provides endpoints to retrieve the number of pending vehicle transactions
 * based on weight type (gross or tare) and direction (inbound or outbound).
 */
@RestController
@RequestMapping("/api/v1/status")
public class VehicleTransactionStatusController {

    private final VehicleTransactionStatusService vehicleTransactionStatusService;

    /**
     * Constructor to inject the `VehicleTransactionStatusService` dependency.
     *
     * @param vehicleTransactionStatusService - The service class responsible for accessing and manipulating
     *                                        vehicle transaction status data.
     */
    public VehicleTransactionStatusController(VehicleTransactionStatusService vehicleTransactionStatusService) {
        this.vehicleTransactionStatusService = vehicleTransactionStatusService;
    }

    /**
     * Retrieves the number of pending inbound transactions with gross weight.
     *
     * @return A ResponseEntity object with status code OK (200) containing the count of pending inbound gross weight transactions.
     */
    @GetMapping("/pendingGross/Inbound")
    public ResponseEntity<Long> noOfInboundGrossWeight() {
        Long pending = vehicleTransactionStatusService.countInboundPendingAction();
        return ResponseEntity.ok(pending);
    }

    /**
     * Retrieves the number of pending outbound transactions with gross weight.
     *
     * @return A ResponseEntity object with status code OK (200) containing the count of pending outbound gross weight transactions.
     */
    @GetMapping("/pendingGross/Outbound")
    public ResponseEntity<Long> noOfOutboundGrossWeight() {
        Long pendingGross = vehicleTransactionStatusService.countOutBoundPendingGross();
        return ResponseEntity.ok(pendingGross);
    }

    /**
     * Retrieves the number of pending outbound transactions with tare weight.
     *
     * @return A ResponseEntity object with status code OK (200) containing the count of pending outbound tare weight transactions.
     */
    @GetMapping("/PendingTare/Outbound")
    public ResponseEntity<Long> noOfOutboundTareWeight() {
        Long pendingTare = vehicleTransactionStatusService.countOutBoundPendingTare();
        return ResponseEntity.ok(pendingTare);
    }


    @GetMapping("/pendingTare/Inbound")
    public ResponseEntity<Long> noOfInboundTareWeight() {
        Long pending =vehicleTransactionStatusService.countInboundPendingTare();
        return ResponseEntity.ok(pending);
    }
}