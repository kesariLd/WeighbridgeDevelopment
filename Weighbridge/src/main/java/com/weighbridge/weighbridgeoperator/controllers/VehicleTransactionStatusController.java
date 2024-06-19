package com.weighbridge.weighbridgeoperator.controllers;

import com.weighbridge.admin.entities.UserMaster;
import com.weighbridge.admin.exceptions.ResourceNotFoundException;
import com.weighbridge.admin.repsitories.UserMasterRepository;
import com.weighbridge.weighbridgeoperator.services.VehicleTransactionStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

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

    private final UserMasterRepository userMasterRepository;

    /**
     * Constructor to inject the `VehicleTransactionStatusService` dependency.
     *
     * @param vehicleTransactionStatusService - The service class responsible for accessing and manipulating
     *                                        vehicle transaction status data.
     */
    public VehicleTransactionStatusController(VehicleTransactionStatusService vehicleTransactionStatusService, UserMasterRepository userMasterRepository) {
        this.vehicleTransactionStatusService = vehicleTransactionStatusService;
        this.userMasterRepository = userMasterRepository;
    }

    @GetMapping("/pendingGross/Inbound")
    public ResponseEntity<Long> noOfInboundGrossWeight(@RequestParam String userId) {
        UserMaster byId = userMasterRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("logged userId invalid"));
        Long pending = vehicleTransactionStatusService.countInboundPendingAction(byId.getSite().getSiteId(),byId.getCompany().getCompanyId());
        return ResponseEntity.ok(pending);
    }

    /**
     * Retrieves the number of pending outbound transactions with gross weight.
     *
     * @return A ResponseEntity object with status code OK (200) containing the count of pending outbound gross weight transactions.
     */
    @GetMapping("/pendingGross/Outbound")
    public ResponseEntity<Long> noOfOutboundGrossWeight(@RequestParam String userId) {
        UserMaster byId = userMasterRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("logged userId invalid"));
        Long pendingGross = vehicleTransactionStatusService.countOutBoundPendingGross(byId.getSite().getSiteId(),byId.getCompany().getCompanyId());
        return ResponseEntity.ok(pendingGross);
    }

    /**
     * Retrieves the number of pending outbound transactions with tare weight.
     *
     * @return A ResponseEntity object with status code OK (200) containing the count of pending outbound tare weight transactions.
     */
    @GetMapping("/PendingTare/Outbound")
    public ResponseEntity<Long> noOfOutboundTareWeight(@RequestParam String userId) {
        UserMaster byId = userMasterRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("logged userId invalid"));
        Long pendingTare = vehicleTransactionStatusService.countOutBoundPendingTare(byId.getSite().getSiteId(),byId.getCompany().getCompanyId());
        return ResponseEntity.ok(pendingTare);
    }

    /**
     * Retrieves the number of pending inbound transactions with tare weight.
     *
     * Tare weight refers to the weight of the vehicle without cargo.
     *
     * @return A ResponseEntity object with status code OK (200) containing the count of pending inbound tare weight transactions.
     */
    @GetMapping("/pendingTare/Inbound")
    public ResponseEntity<Long> noOfInboundTareWeight(@RequestParam String userId) {
        UserMaster byId = userMasterRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("logged userId invalid"));
        Long pending =vehicleTransactionStatusService.countInboundPendingTare(byId.getSite().getSiteId(),byId.getCompany().getCompanyId());
        return ResponseEntity.ok(pending);
    }
}