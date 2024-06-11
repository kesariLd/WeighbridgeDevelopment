package com.weighbridge.admin.repsitories;

import com.weighbridge.admin.entities.CustomerMaster;
import com.weighbridge.admin.entities.SupplierMaster;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SupplierMasterRepository extends JpaRepository<SupplierMaster,Long> {


    boolean existsBySupplierEmailAndSupplierIdNot(String supplierEmail, long id);

    boolean existsBySupplierContactNoOrSupplierEmail(String emailId, String contactNo);


    @Query("SELECT s.supplierName,s.supplierAddressLine1 from SupplierMaster s where s.supplierId =:supplierId")
    Object[] findSupplierNameAndAddressBySupplierId(@Param("supplierId") long supplierId);

    @Query("SELECT s.supplierAddressLine1,s.supplierAddressLine2 from SupplierMaster s where s.supplierName =:supplierName")
    List<String> findSupplierAddressBySupplierName(@Param("supplierName") String supplierName);

    @Query("SELECT s.supplierId FROM SupplierMaster s WHERE s.supplierName = :supplierName " +
            "AND (:addressLine1 IS NULL OR s.supplierAddressLine1 = :addressLine1) " +
            "AND (:addressLine2 IS NULL OR s.supplierAddressLine2 = :addressLine2)")
    Long findSupplierIdBySupplierNameAndAddressLines(
            @Param("supplierName") String supplierName,
            @Param("addressLine1") String addressLine1,
            @Param("addressLine2") String addressLine2);

	SupplierMaster findBySupplierId(long supplierId);

    @Query("SELECT sm.supplierName FROM SupplierMaster sm WHERE sm.supplierId = :supplierId")
    String findSupplierNameBySupplierId(@Param("supplierId") long supplierId);

    @Query("SELECT sm.supplierAddressLine1 FROM SupplierMaster sm WHERE sm.supplierName = :supplierName")
    List<String> findSupplierAddressLine1BySupplierName(@Param("supplierName") String supplierName);

    @Query("SELECT s FROM SupplierMaster s WHERE s.supplierName LIKE %:supplierName% OR s.supplierAddressLine1 LIKE %:supplierAddressLine1% OR s.supplierAddressLine2 LIKE %:supplierAddressLine1%")
    List<SupplierMaster> findBySupplierNameContainingOrSupplierAddressLine1Containing(@Param("supplierName") String supplierName, @Param("supplierAddressLine1") String supplierAddressLine1);


    @Query("SELECT sm.supplierName FROM SupplierMaster sm WHERE sm.supplierStatus= 'ACTIVE' ")
    List<String> findListSupplierName();


    @Query("SELECT s.supplierId FROM SupplierMaster s WHERE s.supplierName= :supplierName")
    List<Long> findListSupplierIdBySupplierName(@Param("supplierName") String supplierName);

    @Query("SELECT s.supplierName, s.supplierAddressLine1, s.supplierAddressLine2 FROM SupplierMaster s where s.supplierId =:supplierId")
    Object[] findSupplierNameAndSupplierAddressesBySupplierId(@Param("supplierId") long supplierId);
}