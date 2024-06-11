package com.weighbridge.SalesManagement.repositories;

import com.weighbridge.SalesManagement.entities.SalesProcess;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalesProcessRepository extends JpaRepository<SalesProcess,String>{

  //   Boolean existsByPurchasePassNo();

    List<SalesProcess> findByPurchaseSaleSaleOrderNo(String saleOrderNo);

    Long countByPurchaseSaleSaleOrderNo(String saleOrderNo);

    SalesProcess findBySalePassNo(String tpNo);


    Page<SalesProcess> findAllByStatusAndPurchaseSaleSiteIdAndPurchaseSaleCompanyId(boolean b, String userSite, String userCompany, Pageable pageable);
}