package com.weighbridge.gateuser.services.impl;

import com.weighbridge.gateuser.repositories.VehicleTransactionStatusRepository;
import com.weighbridge.gateuser.services.VehicleTransactionStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VehicleTransactionStatusImpl implements VehicleTransactionStatusService {

    @Autowired
    VehicleTransactionStatusRepository vehicleTransactionStatusRepository;

    public Long countInboundPendingAction(){
        Long noOfVehicles = vehicleTransactionStatusRepository.countInboundPendingGrossWeight();
        return noOfVehicles;
    }

    public Long countInboundPendingTare(){
        Long pendingTare = vehicleTransactionStatusRepository.countInboundPendingTareWeight();
        return pendingTare;
    }

    @Override
    public Long countOutBoundPendingGross() {
        Long pendingGross = vehicleTransactionStatusRepository.countOutboundPendingGrossWeight();
        return pendingGross;
    }

    @Override
    public Long countOutBoundPendingTare() {
        Long pendingTare = vehicleTransactionStatusRepository.countOutboundPendingTareWeight();
        return pendingTare;
    }
}
