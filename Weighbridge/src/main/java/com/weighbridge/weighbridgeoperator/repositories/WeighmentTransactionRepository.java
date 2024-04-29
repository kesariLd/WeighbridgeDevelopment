package com.weighbridge.weighbridgeoperator.repositories;

import com.weighbridge.weighbridgeoperator.entities.WeighmentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WeighmentTransactionRepository extends JpaRepository<WeighmentTransaction,Integer> {


   WeighmentTransaction findByTicketTicketNo(Integer ticketNo);
}
