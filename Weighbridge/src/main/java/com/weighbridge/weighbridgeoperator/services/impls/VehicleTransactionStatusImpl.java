package com.weighbridge.weighbridgeoperator.services.impls;

import com.weighbridge.weighbridgeoperator.repositories.VehicleTransactionStatusRepository;
import com.weighbridge.weighbridgeoperator.repositories.WeighmentTransactionRepository;
import com.weighbridge.weighbridgeoperator.services.VehicleTransactionStatusService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

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

    private final WeighmentTransactionRepository weighmentTransactionRepository;

    /**
     * Constructor to inject the `VehicleTransactionStatusRepository` dependency.
     *
     * @param vehicleTransactionStatusRepository - The repository class responsible for accessing vehicle transaction status data.
     */
    public VehicleTransactionStatusImpl(VehicleTransactionStatusRepository vehicleTransactionStatusRepository, WeighmentTransactionRepository weighmentTransactionRepository) {
        this.vehicleTransactionStatusRepository = vehicleTransactionStatusRepository;
        this.weighmentTransactionRepository = weighmentTransactionRepository;
    }

    /**
     * Retrieves the number of pending inbound transactions with gross weight.
     *
     * @return The number of pending inbound gross weight transactions.
     */
    @Override
    public Long countInboundPendingAction(String siteId,String companyId) {
        Long noOfVehicles = vehicleTransactionStatusRepository.countInboundPendingGrossWeight(siteId,companyId);
        return noOfVehicles;
    }

    /**
     * Retrieves the number of pending inbound transactions with tare weight.
     *
     * Tare weight refers to the weight of the vehicle without cargo.
     *
     * @return The number of pending inbound tare weight transactions.
     */



    /**
     * Retrieves the number of pending outbound transactions with gross weight.
     *
     * @return The number of pending outbound gross weight transactions.
     */
    @Override
    public Long countOutBoundPendingGross(String siteId,String companyId) {
        Long pendingGross = weighmentTransactionRepository.countOutBoundPendingGrossWeight(siteId,companyId);
        return pendingGross;
    }

    /**
     * Retrieves the number of pending outbound transactions with tare weight.
     *
     * @return The number of pending outbound tare weight transactions.
     */
    @Override
    public Long countOutBoundPendingTare(String siteId,String companyId) {
        Long pendingTare = vehicleTransactionStatusRepository.countOutboundPendingTareWeight(siteId,companyId);
        return pendingTare;
    }


    /**
     * Retrieves the number of pending inbound transactions with tare weight.
     *
     * Tare weight refers to the weight of the vehicle without cargo.
     *
     * @return A ResponseEntity object with status code OK (200) containing the count of pending inbound tare weight transactions.
     */
    public Long countInboundPendingTare(String siteId,String companyId) {
        Long pendingTare = weighmentTransactionRepository.countInboundTransactionsWithZeroNetWeight(siteId,companyId);
        return pendingTare;
    }
}