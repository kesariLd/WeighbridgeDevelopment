package com.weighbridge.weighbridgeoperator.services;




/**
 * Service interface for managing vehicle transaction statuses.
 *
 * This interface defines methods to retrieve the number of pending vehicle transactions
 * based on weight type (gross or tare) and direction (inbound or outbound).
 * Implementations of this interface should provide the logic to access and
 * process the underlying data.
 */
public interface VehicleTransactionStatusService {

    /**
     * Retrieves the number of pending inbound transactions with gross weight.
     *
     * @return The number of pending inbound gross weight transactions.
     */
    public Long countInboundPendingAction(String siteId,String companyId);

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
    public Long countOutBoundPendingGross(String siteId,String companyId);

    /**
     * Retrieves the number of pending outbound transactions with tare weight.
     *
     * @return The number of pending outbound tare weight transactions.
     */
    public Long countOutBoundPendingTare(String siteId,String companyId);

    public Long countInboundPendingTare(String siteId,String companyId);
}