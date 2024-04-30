package com.weighbridge.admin.repsitories;

import com.weighbridge.admin.entities.TransporterMaster;
import com.weighbridge.admin.entities.VehicleMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VehicleMasterRepository extends JpaRepository<VehicleMaster,Long> {

    @Query("SELECT DISTINCT v.id FROM VehicleMaster v WHERE v.vehicleNo = :vehicleNo")
    long findVehicleIdByVehicleNo(@Param("vehicleNo") String vehicleNo);
    @Query("SELECT v.id FROM VehicleMaster v JOIN v.transporter t WHERE v.vehicleNo = :vehicleNo AND t.id = :transporterId")
    long findVehicleByVehicleNoAndTransporterId(@Param("vehicleNo") String vehicleNo, @Param("transporterId") long transporterId);

    @Query("SELECT DISTINCT v.vehicleNo, v.vehicleType, v.vehicleWheelsNo FROM VehicleMaster v WHERE v.id = :id")
    Object[] findDistinctVehicleInfoByVehicleId(@Param("id") long id);

    @Query("SELECT v.transporter FROM VehicleMaster v WHERE v.vehicleNo = :vehicleId")
    List<TransporterMaster> findTransportersByVehicleId(String vehicleId);

//    VehicleMaster findByVehicleNo(String vehicleNo);
//
//    boolean existsByVehicleNo(String vehicleNo);

    List<VehicleMaster> findVehicleMastersByTransporterId(Long transporterId);

    VehicleMaster findByVehicleNo(String vehicleNo);


}
