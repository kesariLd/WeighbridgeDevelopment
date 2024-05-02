package com.weighbridge.weighbridgeoperator.repositories;

import com.weighbridge.weighbridgeoperator.entities.WeighmentTransaction;
import com.weighbridge.weighbridgeoperator.payloads.WeighmentTransactionResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WeighmentTransactionRepository extends JpaRepository<WeighmentTransaction,Integer> {


   WeighmentTransaction findByGateEntryTransactionTicketNo(Integer ticketNo);

   @Query(value = "SELECT g.ticket_no, w.weighment_no, g.transaction_type, g.transaction_date, " +
           "w.gross_weight, w.tare_weight, w.net_weight, " +
           "v.vehicle_no, v.vehicle_fitness_up_to, " +
           "s.supplier_name, t.transporter_name, " +
           "m.material_name " +
           "FROM gate_entry_transaction g " +
           "LEFT JOIN weighment_transaction w ON g.ticket_no = w.ticket_no " +
           "INNER JOIN vehicle_master v ON v.id = g.vehicle_id " +
           "INNER JOIN supplier_master s ON s.supplier_id = g.supplier_id " +
           "INNER JOIN transporter_master t ON t.id = g.transporter_id " +
           "INNER JOIN material_master m ON m.material_id = g.material_id " +
           "WHERE g.site_id = :siteId " +
           "ORDER BY g.transaction_date DESC", nativeQuery = true)
   List<Object[]> getAllGateEntries(@Param("siteId") String siteId);





}
