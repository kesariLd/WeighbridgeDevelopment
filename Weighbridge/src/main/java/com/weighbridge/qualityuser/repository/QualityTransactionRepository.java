package com.weighbridge.qualityuser.repository;

import com.weighbridge.qualityuser.entites.QualityTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface QualityTransactionRepository extends JpaRepository<QualityTransaction,Integer> {

    @Query("SELECT qt FROM QualityTransaction qt WHERE qt.gateEntryTransaction.ticketNo = :ticketNo")
    QualityTransaction findByTicketNo(@Param("ticketNo") Integer ticketNo);

    @Query("SELECT qt FROM QualityTransaction qt WHERE qt.gateEntryTransaction.ticketNo IN :ticketNos")
    List<QualityTransaction> findByGateEntryTransactionTicketNoIn(@Param("ticketNos") List<Integer> gateEntryTransactionTicketNos);

    QualityTransaction findByGateEntryTransactionTicketNo(Integer ticketNo);

    @Query("SELECT qt FROM QualityTransaction qt WHERE qt.gateEntryTransaction.companyId = :companyId AND qt.gateEntryTransaction.siteId = :siteId AND qt.gateEntryTransaction.transactionDate = :transactionDate")
    List<QualityTransaction> findByGateEntryTransactionCompanyIdAndSiteIdAndTransactionDate(@Param("companyId") String companyId, @Param("siteId") String siteId, @Param("transactionDate") LocalDate transactionDate);


    @Query("SELECT COUNT(qt.gateEntryTransaction) FROM QualityTransaction qt WHERE qt.gateEntryTransaction.transactionDate BETWEEN :startDate AND :endDate AND qt.gateEntryTransaction.siteId=:siteId AND qt.gateEntryTransaction.companyId=:companyId AND qt.gateEntryTransaction.transactionType=:transactionType")
    Long countInboundQuality(String transactionType,LocalDate startDate,LocalDate endDate,String siteId,String companyId);


}


