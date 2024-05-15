package com.weighbridge.SalesManagement.repositories;

import com.weighbridge.SalesManagement.entities.SalesOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesOrderRespository extends JpaRepository<SalesOrder,String> {

    Boolean existsBySaleOrderNo(String saleOrderNo);

    SalesOrder findByPurchaseOrderNo(String purchaseOrderNo);

    Long countByPurchaseOrderNoStartingWith(String formattedDate);

    SalesOrder findBySaleOrderNo(String s);
}