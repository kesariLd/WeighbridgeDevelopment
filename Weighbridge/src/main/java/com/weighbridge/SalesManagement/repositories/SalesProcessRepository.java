package com.weighbridge.SalesManagement.repositories;

import com.weighbridge.SalesManagement.entities.SalesProcess;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesProcessRepository extends JpaRepository<SalesProcess,String>{

  //   Boolean existsByPurchasePassNo();

     Long countByPurchaseSalePurchaseOrderNo(String purchaseOrderNo);

    SalesProcess findByPurchasePassNo(String tpNo);

    SalesProcess findByPurchaseSalePurchaseOrderNo(String purchaseOrderNo);
}