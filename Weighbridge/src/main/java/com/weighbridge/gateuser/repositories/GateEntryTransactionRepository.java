package com.weighbridge.gateuser.repositories;

import com.weighbridge.gateuser.entities.GateEntryTransaction;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Repository interface for accessing gate entry transaction data.
 */
public interface GateEntryTransactionRepository extends JpaRepository<GateEntryTransaction, Integer>, JpaSpecificationExecutor<GateEntryTransaction> {

    /**
     * Retrieves a list of gate entry transactions based on the specified site ID and company ID.
     *
     * @param siteId    The ID of the site to filter gate entry transactions.
     * @param companyId The ID of the company to filter gate entry transactions.
     * @return A list of gate entry transactions matching the provided site ID and company ID.
     */
//    Page<GateEntryTransaction> findBySiteIdAndCompanyIdOrderByTicketNoDesc(String siteId, String companyId ,Pageable pageable);
    //Pagination get All user
    Page<GateEntryTransaction> findBySiteIdAndCompanyIdAndVehicleOutIsNull(Pageable pageable,String siteId, String companyId);


    Page<GateEntryTransaction> findBySiteIdAndCompanyIdAndVehicleOutIsNotNull(Pageable pageable,String siteId, String companyId);


    // Modified method to find by siteId, companyId, and transactionDate within the specified range
    List<GateEntryTransaction> findBySiteIdAndCompanyIdAndTransactionDateBetweenOrderByTransactionDateDesc(String siteId, String companyId, LocalDate startDate, LocalDate endDate);

    GateEntryTransaction findByTicketNo(Integer ticketNo);


    List<GateEntryTransaction>findByTransactionTypeAndSiteIdAndCompanyIdOrderByTransactionDate(String transactionType,String userSite,String userCompany);


    @Query("SELECT g FROM GateEntryTransaction g WHERE g.supplierId = :supplierId ORDER BY g.ticketNo DESC")
    List<GateEntryTransaction> findBySupplierIdOrderByTicketNoDesc(Long supplierId);

    List<GateEntryTransaction> findByVehicleIdOrderByTicketNo(long id);


    @Query("SELECT g FROM GateEntryTransaction g WHERE g.ticketNo = :ticketNo AND g.companyId = :companyId AND g.siteId = :siteId")
    GateEntryTransaction findByTicketNoAndCompanyIdAndSiteId(Integer ticketNo, String companyId, String siteId);

    List<GateEntryTransaction> findBySiteIdAndCompanyIdOrderByTransactionDateDesc(String userSite, String userCompany);

    @Query("SELECT g FROM GateEntryTransaction g WHERE g.customerId = :customerId ORDER BY g.ticketNo DESC")
    List<GateEntryTransaction> findByCustomerIdOrderByTicketNoDesc(Long customerId);


    @Query("SELECT new map(g.transactionDate as transactionDate, " +
            "SUM(CASE WHEN g.transactionType = 'Inbound' THEN 1 ELSE 0 END) as inboundCount, " +
            "SUM(CASE WHEN g.transactionType = 'Outbound' THEN 1 ELSE 0 END) as outboundCount) " +
            "FROM GateEntryTransaction g " +
            "WHERE g.transactionDate BETWEEN :startDate AND :endDate " +
            "AND g.companyId = :companyId " +
            "AND g.siteId = :siteId " +
            "AND g.vehicleOut IS NOT NULL " +
            "GROUP BY g.transactionDate")
    List<Map<String, Object>> findByTransactionStartDateAndTransactionEndDateCompanySite(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("companyId") String companyId,
            @Param("siteId") String siteId
    );

    // TODO Add Company and site
    @Query("SELECT count(g.ticketNo) FROM GateEntryTransaction g " +
            "WHERE g.transactionType = 'Inbound' AND g.vehicleOut IS NULL " +
            "AND g.siteId = :siteId AND g.companyId = :companyId")
    Long countPendingGateTransactionsInbound(@Param("siteId") String siteId, @Param("companyId") String companyId);



    @Query("SELECT count(g.ticketNo) FROM GateEntryTransaction g WHERE g.transactionType = 'Outbound' AND g.vehicleOut IS NULL AND g.siteId = :siteId AND g.companyId = :companyId")
    Long countPendingGateTransactionsOutbound(@Param("siteId") String siteId, @Param("companyId") String companyId);

    @Query("SELECT count(g.ticketNo) FROM GateEntryTransaction g where g.vehicleOut IS Not null AND g.siteId = :siteId AND g.companyId = :companyId")
    Long countCompleteGateTransaction(@Param("siteId") String siteId, @Param("companyId") String companyId);


    @Query("SELECT count(g.ticketNo) FROM GateEntryTransaction g WHERE g.transactionType=:transactionType AND g.vehicleOut IS NULL AND g.transactionDate BETWEEN :startDate AND :endDate AND g.siteId=:siteId AND g.companyId=:companyId")
    Long countGateEntryWithDate(@Param("transactionType") String transactionType,@Param("startDate") LocalDate startDate,@Param("endDate") LocalDate endDate,@Param("companyId") String companyId,@Param("siteId") String siteId);

    @Query("SELECT count(g.ticketNo) FROM GateEntryTransaction g WHERE g.transactionType=:transactionType AND g.vehicleOut IS Not NULL AND g.transactionDate BETWEEN :startDate AND :endDate AND g.siteId=:siteId AND g.companyId=:companyId")
    Long countGateExitWithDate(@Param("transactionType") String transactionType,@Param("startDate") LocalDate startDate,@Param("endDate") LocalDate endDate,@Param("companyId") String companyId,@Param("siteId") String siteId);


    @Query("SELECT g.ticketNo FROM GateEntryTransaction g WHERE g.companyId = :companyId AND g.siteId = :siteId AND g.supplierId = :supplierId AND g.transactionDate = :transactionDate")
    List<Integer> findTicketNosByCompanyIdAndSiteIdAndSupplierIdAndTransactionDate(@Param("companyId") String companyId, @Param("siteId") String siteId, @Param("supplierId") Long supplierId, @Param("transactionDate") LocalDate date);


}

