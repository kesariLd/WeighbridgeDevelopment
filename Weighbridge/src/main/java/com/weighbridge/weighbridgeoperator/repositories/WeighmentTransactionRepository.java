package com.weighbridge.weighbridgeoperator.repositories;

import com.weighbridge.weighbridgeoperator.entities.WeighmentTransaction;
import com.weighbridge.weighbridgeoperator.payloads.WeighmentTransactionResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WeighmentTransactionRepository extends JpaRepository<WeighmentTransaction,Integer> {


   WeighmentTransaction findByGateEntryTransactionTicketNo(Integer ticketNo);

   @Query("SELECT g.ticketNo, w.weighmentNo, g.transactionType, g.transactionDate, g.vehicleIn, " +
           "w.grossWeight, w.tareWeight, w.netWeight, w.temporaryWeight, " +
           "v.vehicleNo, v.vehicleFitnessUpTo, " +
           "COALESCE(s.supplierName, c.customerName) AS supplierOrCustomer, " +
           "t.transporterName, " +
           "CASE WHEN g.transactionType = 'Inbound' THEN m.materialName " +
           "     WHEN g.transactionType = 'Outbound' THEN p.productName END AS materialOrProduct " +
           "FROM GateEntryTransaction g " +
           "LEFT JOIN WeighmentTransaction w ON g.ticketNo = w.gateEntryTransaction.ticketNo " +
           "INNER JOIN VehicleMaster v ON v.id = g.vehicleId " +
           "INNER JOIN TransporterMaster t ON t.id = g.transporterId " +
           "LEFT JOIN MaterialMaster m ON m.materialId = g.materialId AND g.transactionType = 'Inbound' " +
           "LEFT JOIN ProductMaster p ON p.productId = g.materialId AND g.transactionType = 'Outbound' " +
           "INNER JOIN VehicleTransactionStatus ts ON ts.ticketNo = g.ticketNo " +
           "LEFT JOIN SupplierMaster s ON s.supplierId = g.supplierId " +
           "LEFT JOIN CustomerMaster c ON c.customerId = g.customerId " +
           "WHERE g.siteId = :siteId AND (w.netWeight IS NULL OR w.netWeight = 0.0) " +
           "ORDER BY g.ticketNo DESC")
   List<Object[]> getAllGateEntries(@Param("siteId") String siteId);



}