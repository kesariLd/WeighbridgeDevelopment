package com.weighbridge.weighbridgeoperator.repositories;

import com.weighbridge.weighbridgeoperator.dto.WeighbridgeOperatorSearchCriteria;
import com.weighbridge.weighbridgeoperator.entities.WeighmentTransaction;
import com.weighbridge.weighbridgeoperator.payloads.WeighmentTransactionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface WeighmentTransactionRepository extends JpaRepository<WeighmentTransaction,Integer>, JpaSpecificationExecutor<WeighmentTransaction> {


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
            "WHERE g.siteId = :siteId AND g.companyId=:companyId AND (w.netWeight IS NULL OR w.netWeight = 0.0) " +
            "ORDER BY g.ticketNo DESC")
    Page<Object[]> getAllGateEntries(@Param("siteId") String siteId, @Param("companyId") String companyId, Pageable pageable);

    @Query("FROM WeighmentTransaction wt WHERE wt.gateEntryTransaction.siteId=:userSite AND wt.gateEntryTransaction.companyId=:userCompany AND wt.netWeight!=0.0")
    Page<WeighmentTransaction> findAllByUserSiteAndUserCompany(String userSite, String userCompany, Pageable pageable);

    @Query("SELECT COUNT(wt.netWeight) FROM WeighmentTransaction wt WHERE wt.netWeight!=0.0")
    long countCompletedTransactions();

    @Query("SELECT COUNT(wt.gateEntryTransaction) FROM WeighmentTransaction wt WHERE wt.netWeight = 0.0 AND wt.gateEntryTransaction.transactionType = 'Inbound' AND wt.gateEntryTransaction.siteId=:siteId AND wt.gateEntryTransaction.companyId=:companyId")
    Long countInboundTransactionsWithZeroNetWeight(@Param("siteId")String siteId,@Param("companyId") String companyId);

    @Query("SELECT COUNT(wt.gateEntryTransaction) FROM WeighmentTransaction wt WHERE wt.netWeight = 0.0 AND wt.gateEntryTransaction.transactionType = 'Outbound' AND wt.gateEntryTransaction.siteId=:siteId AND wt.gateEntryTransaction.companyId=:companyId")
    Long countOutBoundPendingGrossWeight(@Param("siteId") String siteId,@Param("companyId") String companyId);

    @Query("SELECT wt FROM WeighmentTransaction wt WHERE wt.gateEntryTransaction.companyId = :companyId AND wt.gateEntryTransaction.siteId = :siteId AND wt.gateEntryTransaction.transactionDate = :transactionDate")
    List<WeighmentTransaction> findByGateEntryTransactionCompanyIdAndSiteIdAndTransactionDate(@Param("companyId") String companyId, @Param("siteId") String siteId, @Param("transactionDate") LocalDate date);

    @Query("SELECT gt.transactionDate, gt.materialId, SUM(wt.netWeight) " +
            "FROM WeighmentTransaction wt " +
            "JOIN wt.gateEntryTransaction gt " +
            "WHERE gt.transactionDate BETWEEN :startDate AND :endDate " +
            "AND gt.transactionType =:transactionType AND gt.siteId = :siteId AND gt.companyId = :companyId " +
            "GROUP BY gt.transactionDate, gt.materialId")
    List<Object[]> findTotalNetWeightByTransactionDateAndMaterialId(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("companyId") String companyId, @Param("siteId") String siteId,@Param("transactionType")String transactionType);

    @Query("SELECT count(wt.gateEntryTransaction) FROM WeighmentTransaction wt WHERE wt.tareWeight!=0.0 AND wt.gateEntryTransaction.transactionDate BETWEEN :startDate AND :endDate AND wt.gateEntryTransaction.siteId=:siteId AND wt.gateEntryTransaction.companyId=:companyId AND wt.gateEntryTransaction.transactionType='Inbound'")
    Long countCompletedInboundTareWeights(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("companyId") String companyId, @Param("siteId") String siteId);

    @Query("SELECT count(wt.gateEntryTransaction) FROM WeighmentTransaction wt WHERE wt.temporaryWeight!=0.0 AND wt.gateEntryTransaction.transactionDate BETWEEN :startDate AND :endDate AND wt.gateEntryTransaction.siteId=:siteId AND wt.gateEntryTransaction.companyId=:companyId AND wt.gateEntryTransaction.transactionType='Outbound'")
    Long countCompletedOutboundTareWeights(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("companyId") String companyId, @Param("siteId") String siteId);

    @Query("SELECT count(wt.gateEntryTransaction) FROM WeighmentTransaction wt WHERE wt.grossWeight!=0.0 AND wt.gateEntryTransaction.transactionDate BETWEEN :startDate AND :endDate AND wt.gateEntryTransaction.siteId=:siteId AND wt.gateEntryTransaction.companyId=:companyId AND wt.gateEntryTransaction.transactionType='Outbound'")
    Long countCompletedGrossWeightsOutbound(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("companyId") String companyId, @Param("siteId") String siteId);

    @Query("SELECT count(wt.gateEntryTransaction) FROM WeighmentTransaction wt WHERE wt.temporaryWeight!=0.0 AND wt.gateEntryTransaction.transactionDate BETWEEN :startDate AND :endDate AND wt.gateEntryTransaction.siteId=:siteId AND wt.gateEntryTransaction.companyId=:companyId AND wt.gateEntryTransaction.transactionType='Outbound'")
    Long countCompletedGrossWeightsInbound(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("companyId") String companyId, @Param("siteId") String siteId);
}