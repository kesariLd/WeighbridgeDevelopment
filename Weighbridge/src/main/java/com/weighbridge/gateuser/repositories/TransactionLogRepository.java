package com.weighbridge.gateuser.repositories;

import com.weighbridge.gateuser.entities.TransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionLogRepository extends JpaRepository<TransactionLog,Integer> {

    TransactionLog findByTicketNo(Integer ticketNo);

    TransactionLog findByTicketNoAndStatusCode(Integer ticketNo,String statusCode);
}
