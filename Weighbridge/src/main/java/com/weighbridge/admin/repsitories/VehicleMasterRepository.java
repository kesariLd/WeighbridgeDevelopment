package com.weighbridge.admin.repsitories;

import com.weighbridge.admin.entities.TransporterMaster;
import com.weighbridge.admin.entities.VehicleMaster;
import com.weighbridge.gateuser.entities.GateEntryTransaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface VehicleMasterRepository extends JpaRepository<VehicleMaster,Long> {

    @Query("SELECT DISTINCT v.id FROM VehicleMaster v WHERE v.vehicleNo = :vehicleNo")
    long findVehicleIdByVehicleNo(@Param("vehicleNo") String vehicleNo);

    @Query("SELECT v.id FROM VehicleMaster v JOIN v.transporter t WHERE v.vehicleNo = :vehicleNo AND t.id = :transporterId")
    long findVehicleByVehicleNoAndTransporterId(@Param("vehicleNo") String vehicleNo, @Param("transporterId") long transporterId);

    @Query("SELECT DISTINCT v.vehicleNo, v.vehicleType, v.vehicleWheelsNo FROM VehicleMaster v WHERE v.id = :id")
    Object[] findDistinctVehicleInfoByVehicleId(@Param("id") long id);

    @Query("SELECT DISTINCT v.vehicleNo, v.vehicleType, v.vehicleWheelsNo, v.vehicleFitnessUpTo FROM VehicleMaster v WHERE v.id = :id")
    Object[] findDistinctVehicleInfoVehicleNoVehicleTypeFitnessByVehicleId(@Param("id") long id);

    @Query("SELECT v.transporter FROM VehicleMaster v WHERE v.vehicleNo = :vehicleId")
    List<TransporterMaster> findTransportersByVehicleId(String vehicleId);

    VehicleMaster findByVehicleNo(String vehicleNo);

    @Query("SELECT v.vehicleNo, v.vehicleWheelsNo, v.vehicleFitnessUpTo,v.vehicleType, t.transporterName " +
            "FROM VehicleMaster v JOIN v.transporter t WHERE v.vehicleNo = :vehicleNo")
    Set<Object[]> findVehicleInfoByVehicleNo(String vehicleNo);

    @Query("SELECT v FROM VehicleMaster v JOIN v.transporter t WHERE v.vehicleNo = :vehicleNo AND t.transporterName = :transporterName")
    VehicleMaster findByVehicleNoAndTransporterMasterTransporterName(String vehicleNo, String transporterName);

    @Query("SELECT vm.vehicleNo FROM VehicleMaster vm WHERE vm.id = :vehicleId")
    String findVehicleNoById(@Param("vehicleId") long vehicleId);

    @Query("SELECT vm.vehicleNo FROM VehicleMaster vm WHERE vm.id IN (SELECT DISTINCT ge.vehicleId FROM GateEntryTransaction ge)")
    List<String> findVehicleNosInGateEntryTransactions();

    @Query("SELECT vm.vehicleFitnessUpTo FROM VehicleMaster vm WHERE vm.id = :vehicleId")
    LocalDate findVehicleFitnessById(@Param("vehicleId") long vehicleId);
}