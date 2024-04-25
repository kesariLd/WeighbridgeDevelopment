package com.weighbridge.gateuser.repositories;

import com.weighbridge.gateuser.entities.VehicleTransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VehicleTransactionStatusRepository extends JpaRepository<VehicleTransactionStatus,Integer> {


    VehicleTransactionStatus findByTicketNo(Integer ticketNo);
}
