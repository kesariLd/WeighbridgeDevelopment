package com.weighbridge.weighbridgeoperator.repositories;

import com.weighbridge.weighbridgeoperator.entities.VehicleTransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

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

    @Query(value = "select count(status_code) FROM vehicle_transaction_status as ts inner join gate_entry_transaction as g on ts.ticket_no=g.ticket_no where ts.status_code='GNT' and g.transaction_type='Inbound' and g.site_id=:siteId and g.company_id=:companyId",nativeQuery = true)
    Long countInboundPendingGrossWeight(@Param("siteId") String siteId,@Param("companyId")String companyId);

    @Query(value = "select count(status_code) FROM vehicle_transaction_status as ts inner join gate_entry_transaction as g on ts.ticket_no=g.ticket_no where ts.status_code='GNT' and g.transaction_type='Outbound' and g.site_id=:siteId and g.company_id=:companyId",nativeQuery = true)
    Long countOutboundPendingTareWeight(@Param("siteId") String siteId,@Param("companyId")String companyId);

    List<VehicleTransactionStatus> findByStatusCodeAndTicketNo(String gwt, Integer ticketNo);
}

    

