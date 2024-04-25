package com.weighbridge.admin.repsitories;

import com.weighbridge.admin.entities.StatusCodeMaster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusCodeMasterRepository extends JpaRepository<StatusCodeMaster,String > {
}
