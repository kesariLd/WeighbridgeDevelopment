package com.weighbridge.gateuser.services.impl;

import com.weighbridge.gateuser.repositories.VehicleTransactionStatusRepository;
import com.weighbridge.gateuser.services.VehicleTransactionStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation class for the `VehicleTransactionStatusService` interface.
 *
 * This class provides methods to retrieve the number of pending vehicle transactions
 * based on weight type (gross or tare) and direction (inbound or outbound).
 * It interacts with the `VehicleTransactionStatusRepository` to access the underlying data.
 */
@Service
public class VehicleTransactionStatusImpl implements VehicleTransactionStatusService {

    private final VehicleTransactionStatusRepository vehicleTransactionStatusRepository;

    /**
     * Constructor to inject the `VehicleTransactionStatusRepository` dependency.
     *
     * @param vehicleTransactionStatusRepository - The repository class responsible for accessing vehicle transaction status data.
     */
    public VehicleTransactionStatusImpl(VehicleTransactionStatusRepository vehicleTransactionStatusRepository) {
        this.vehicleTransactionStatusRepository = vehicleTransactionStatusRepository;
    }

    /**
     * Retrieves the number of pending inbound transactions with gross weight.
     *
     * @return The number of pending inbound gross weight transactions.
     */
    @Override
    public Long countInboundPendingAction() {
        Long noOfVehicles = vehicleTransactionStatusRepository.countInboundPendingGrossWeight();
        return noOfVehicles;
    }

    /**
     * Retrieves the number of pending inbound transactions with tare weight.
     *
     * Tare weight refers to the weight of the vehicle without cargo.
     *
     * @return The number of pending inbound tare weight transactions.
     */
    @Override
    public Long countInboundPendingTare() {
        Long pendingTare = vehicleTransactionStatusRepository.countInboundPendingTareWeight();
        return pendingTare;
    }

    /**
     * Retrieves the number of pending outbound transactions with gross weight.
     *
     * @return The number of pending outbound gross weight transactions.
     */
    @Override
    public Long countOutBoundPendingGross() {
        Long pendingGross = vehicleTransactionStatusRepository.countOutboundPendingGrossWeight();
        return pendingGross;
    }

    /**
     * Retrieves the number of pending outbound transactions with tare weight.
     *
     * @return The number of pending outbound tare weight transactions.
     */
    @Override
    public Long countOutBoundPendingTare() {
        Long pendingTare = vehicleTransactionStatusRepository.countOutboundPendingTareWeight();
        return pendingTare;
    }
}

