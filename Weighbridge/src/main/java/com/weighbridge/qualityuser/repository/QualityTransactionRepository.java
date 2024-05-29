package com.weighbridge.qualityuser.repository;

import com.weighbridge.qualityuser.entites.QualityTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QualityTransactionRepository extends JpaRepository<QualityTransaction,Integer> {

    @Query("SELECT qt FROM QualityTransaction qt WHERE qt.gateEntryTransaction.ticketNo = :ticketNo")
    QualityTransaction findByTicketNo(@Param("ticketNo") Integer ticketNo);
  
    QualityTransaction findByGateEntryTransactionTicketNo(@Param("ticketNo") Integer ticketNo);
}
