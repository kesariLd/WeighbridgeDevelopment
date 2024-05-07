package com.weighbridge.admin.repsitories;

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
    Object[] findCustomerNameBycustomerId(@Param("customerId") long customerId);
}
