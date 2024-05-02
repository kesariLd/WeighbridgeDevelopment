package com.weighbridge.gateuser.repositories;

import com.weighbridge.gateuser.entities.VehicleTransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VehicleTransactionStatusRepository extends JpaRepository<VehicleTransactionStatus,Integer> {


    VehicleTransactionStatus findByTicketNo(Integer ticketNo);

    VehicleTransactionStatus findByTicketNoAndStatusCode(Integer ticketNo, String gnt);

    @Query(value = "select count(status_code) FROM `weighbridge-test`.vehicle_transaction_status as ts inner join `weighbridge-test`.gate_entry_transaction as g on ts.ticket_no=g.ticket_no where ts.status_code='GNT' and g.transaction_type='Inbound'",nativeQuery = true)
    Long countInboundPendingGrossWeight();

    @Query(value = "select count(status_code) FROM `weighbridge-test`.vehicle_transaction_status as ts inner join `weighbridge-test`.gate_entry_transaction as g on ts.ticket_no=g.ticket_no where ts.status_code='GWT' and g.transaction_type='Inbound'",nativeQuery = true)
    Long countInboundPendingTareWeight();


    @Query(value = "select count(status_code) FROM `weighbridge-test`.vehicle_transaction_status as ts inner join `weighbridge-test`.gate_entry_transaction as g on ts.ticket_no=g.ticket_no where ts.status_code='TWT' and g.transaction_type='Outbound'",nativeQuery = true)
    Long countOutboundPendingGrossWeight();

    @Query(value = "select count(status_code) FROM `weighbridge-test`.vehicle_transaction_status as ts inner join `weighbridge-test`.gate_entry_transaction as g on ts.ticket_no=g.ticket_no where ts.status_code='GNT' and g.transaction_type='Outbound'",nativeQuery = true)
    Long countOutboundPendingTareWeight();
}