package com.weighbridge.admin.repsitories;

import com.weighbridge.admin.entities.ProductTypeMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductTypeMasterRepository extends JpaRepository<ProductTypeMaster, Long> {
    @Query("SELECT ptm FROM ProductTypeMaster ptm WHERE ptm.productTypeName = :productTypeName")
    ProductTypeMaster findByProductTypeName(@Param("productTypeName") String productTypeName);

    @Query("SELECT ptm.productTypeName FROM ProductTypeMaster ptm WHERE ptm.productMaster.productName = :productName")
    List<String> findByProductMasterProductName(@Param("productName") String productName);

    @Query("SELECT p.productName FROM ProductMaster p WHERE p.productId = :productId")
    String findProductNameByProductId(@Param("productId") long productId);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END FROM ProductTypeMaster p WHERE p.productTypeName = :productTypeName AND p.productMaster.productId = :productId")
    boolean existsByProductTypeNameAndProductMasterProductId(@Param("productTypeName") String productTypeName, @Param("productId") long productId);
}
