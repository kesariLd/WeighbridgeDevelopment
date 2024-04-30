package com.weighbridge.gateuser.repositories;

import com.weighbridge.gateuser.entities.VehicleTransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository interface for accessing vehicle transaction status data.
 */
public interface VehicleTransactionStatusRepository extends JpaRepository<VehicleTransactionStatus, Integer> {

    /**
     * Retrieves a vehicle transaction status entry based on the specified ticket number.
     *
     * @param ticketNo The ticket number to search for.
     * @return The vehicle transaction status entry corresponding to the provided ticket number, if found.
     */
    VehicleTransactionStatus findByTicketNo(Integer ticketNo);

    /**
     * Retrieves a vehicle transaction status entry based on the specified ticket number and status code.
     *
     * @param ticketNo   The ticket number to search for.
     * @param statusCode The status code to search for.
     * @return The vehicle transaction status entry corresponding to the provided ticket number and status code, if found.
     */
    VehicleTransactionStatus findByTicketNoAndStatusCode(Integer ticketNo, String statusCode);
}

