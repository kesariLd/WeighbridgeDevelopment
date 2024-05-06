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

    VehicleTransactionStatus findByTicketNoAndStatusCode(Integer ticketNo, String gnt);

    boolean existsByStatusCode(String status);

    @Query(value = "select count(status_code) FROM vehicle_transaction_status as ts inner join gate_entry_transaction as g on ts.ticket_no=g.ticket_no where ts.status_code='GNT' and g.transaction_type='Inbound'",nativeQuery = true)
    Long countInboundPendingGrossWeight();

    @Query(value = "select count(status_code) FROM vehicle_transaction_status as ts inner join gate_entry_transaction as g on ts.ticket_no=g.ticket_no where ts.status_code='GWT' and g.transaction_type='Inbound'",nativeQuery = true)
    Long countInboundPendingTareWeight();


    @Query(value = "select count(status_code) FROM vehicle_transaction_status as ts inner join gate_entry_transaction as g on ts.ticket_no=g.ticket_no where ts.status_code='TWT' and g.transaction_type='Outbound'",nativeQuery = true)
    Long countOutboundPendingGrossWeight();

    @Query(value = "select count(status_code) FROM vehicle_transaction_status as ts inner join gate_entry_transaction as g on ts.ticket_no=g.ticket_no where ts.status_code='GNT' and g.transaction_type='Outbound'",nativeQuery = true)
    Long countOutboundPendingTareWeight();
}