package com.weighbridge.admin.repsitories;

import com.weighbridge.admin.entities.TransporterMaster;
import com.weighbridge.gateuser.entities.GateEntryTransaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TransporterMasterRepository extends JpaRepository<TransporterMaster, Long> {


    Boolean existsByTransporterName(String transporterName);

    TransporterMaster findByTransporterName(String transporterName);
    List<TransporterMaster> findTransporterMastersByVehiclesId(Long vehicleId);

    @Query("SELECT t.id FROM TransporterMaster t WHERE t.transporterName = :transporterName")
    long findTransporterIdByTransporterName(@Param("transporterName") String transporterName);

    @Query("SELECT t.transporterName FROM TransporterMaster t WHERE t.id =:id ")
    String findTransporterNameByTransporterId(@Param("id") long id);

	

}
