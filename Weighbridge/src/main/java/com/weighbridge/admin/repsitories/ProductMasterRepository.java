package com.weighbridge.admin.repsitories;

import com.weighbridge.admin.entities.ProductMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductMasterRepository extends JpaRepository<ProductMaster, Long> {
    @Query("SELECT pm FROM ProductMaster pm WHERE pm.productName = :productName")
    ProductMaster findByProductName(@Param("productName") String productName);

    @Query("SElECT pm.productName FROM ProductMaster pm WHERE pm.productStatus = :status")
    List<String> findAllProductNameByProductStatus(@Param("status") String status);

    @Query("SELECT pm.productId FROM ProductMaster pm WHERE pm.productName = :productName")
    long findProductIdByProductName(@Param("productName") String productName);

    @Query("SELECT pm.productName FROM ProductMaster pm WHERE pm.productId = :productId")
    String findProductNameByProductId(@Param("productId") long productId);
}
