package com.weighbridge.admin.repsitories;

import com.weighbridge.admin.entities.CustomerMaster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerMasterRepository extends JpaRepository<CustomerMaster, Long> {
    boolean existsByCustomerContactNoOrCustomerEmail(String customerContactNo, String customerEmail);
}
