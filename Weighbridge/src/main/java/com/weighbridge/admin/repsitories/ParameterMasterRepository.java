package com.weighbridge.admin.repsitories;

import com.weighbridge.admin.entities.ParameterMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ParameterMasterRepository extends JpaRepository<ParameterMaster, Long> {
    @Query("SELECT pm.parameterName FROM ParameterMaster pm")
    List<String> findAllParameterNames();

    @Query("SELECT CASE " +
            "WHEN COUNT(pm) > 0 " +
            "THEN true ELSE false " +
            "END " +
            "FROM ParameterMaster pm " +
            "WHERE pm.parameterName = :parameterName")
    boolean existsByParameterName(String parameterName);

    @Query("SELECT pm FROM ParameterMaster pm WHERE pm.parameterName = :parameterName")
    ParameterMaster findByParameterName(@Param("parameterName") String parameterName);
}

