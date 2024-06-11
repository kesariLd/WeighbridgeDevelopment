package com.weighbridge.admin.repsitories;

import com.weighbridge.admin.entities.TransporterMaster;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransporterMasterRepository extends JpaRepository<TransporterMaster, Long> {
    Boolean existsByTransporterName(String transporterName);

    TransporterMaster findByTransporterName(String transporterName);

    @Query("SELECT t.id FROM TransporterMaster t WHERE t.transporterName = :transporterName")
    long findTransporterIdByTransporterName(@Param("transporterName") String transporterName);

    @Query("SELECT t.transporterName FROM TransporterMaster t WHERE t.id =:id ")
    String findTransporterNameByTransporterId(@Param("id") long id);

    @Query("SELECT t.transporterName FROM TransporterMaster t WHERE t.status = 'ACTIVE' ")
    List<String> findAllByTransporterStatus();
}
