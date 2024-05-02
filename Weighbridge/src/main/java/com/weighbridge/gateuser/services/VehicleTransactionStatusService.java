package com.weighbridge.gateuser.services;




public interface VehicleTransactionStatusService {

    public Long countInboundPendingAction();

    public Long countInboundPendingTare();

    public Long countOutBoundPendingGross();

    public Long countOutBoundPendingTare();
}
