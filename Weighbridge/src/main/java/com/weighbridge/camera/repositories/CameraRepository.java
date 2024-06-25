package com.weighbridge.camera.repositories;


import com.weighbridge.camera.entites.CameraView;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CameraRepository extends JpaRepository<CameraView,Long> {
    CameraView findByTicketNoAndRoleId(Integer ticketNo, int i);

    CameraView findByTicketNoAndRoleIdAndTruckStatus(Integer ticketNo, Integer roleId,String truckStatus);
}
