package com.weighbridge.admin.repsitories;

import com.weighbridge.admin.dtos.CustomerMasterDto;
import com.weighbridge.admin.entities.CustomerMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerMasterRepository extends JpaRepository<CustomerMaster, Long> {
    boolean existsByCustomerContactNoOrCustomerEmail(String customerContactNo, String customerEmail);

    @Query("SELECT c.customerId FROM CustomerMaster c WHERE c.customerName = :customerName " +
            "AND (:customerAddressLine1 IS NULL OR c.customerAddressLine1= :customerAddressLine1) " +
            "AND (:customerAddressLine2 IS NULL OR c.customerAddressLine2 = :customerAddressLine2)")
    Long findCustomerIdByCustomerNameAndAddressLines(
            @Param("customerName") String customerName,
            @Param("customerAddressLine1") String customerAddressLine1,
            @Param("customerAddressLine2") String customerAddressLine2);

    @Query("SELECT c.customerAddressLine1,c.customerAddressLine2 from CustomerMaster c where c.customerName =:customerName")
    List<String> findCustomerAddressByCustomerName(@Param("customerName") String customerName);

    @Query("SELECT c.customerName,c.customerAddressLine1 from CustomerMaster c where c.customerId =:customerId")
    Object[] findCustomerNameAndAddressBycustomerId(@Param("customerId") long customerId);

    @Query("SELECT c.customerName,c.customerAddressLine1,c.customerAddressLine2 from CustomerMaster c where c.customerId =:customerId")
    Object[] findCustomerNameAndAddress1andAddress2ByCustomerId(@Param("customerId") long customerId);

    @Query("SELECT cm.customerName FROM CustomerMaster cm WHERE cm.customerId = :customerId")
    String findCustomerNameByCustomerId(@Param("customerId") long customerId);

    CustomerMaster findByCustomerId(long customerId);

    Boolean existsByCustomerEmailAndCustomerIdNot(String emailId,long id);

    @Query("SELECT c.customerName FROM CustomerMaster c WHERE c.customerStatus= 'ACTIVE' ")
    List<String> findListCustomerName();


    @Query("SELECT c.customerId FROM CustomerMaster c WHERE c.customerName= :customerName")
    List<Long> findListCustomerIdbyCustomerName(@Param("customerName") String customerName);
    @Query("SELECT c FROM CustomerMaster c WHERE c.customerName LIKE %:customerName% OR c.customerAddressLine1 LIKE %:customerAddressLine1% OR c.customerAddressLine2 LIKE %:customerAddressLine1%")
    List<CustomerMaster> findByCustomerNameContainingOrCustomerAddressLine1Containing(@Param("customerName") String supplierOrCustomerName, @Param("customerAddressLine1") String supplierOrCustomerAddress);

    @Query("SELECT c.customerName, c.customerAddressLine1, c.customerAddressLine2 FROM CustomerMaster c where c.customerId =:customerId")
    Object[] findCustomerNameAndCustomerAddressesByCustomerId(@Param("customerId") long customerId);
}