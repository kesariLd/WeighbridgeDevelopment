package com.weighbridge.admin.services;

import com.weighbridge.admin.dtos.VehicleMasterDto;
import com.weighbridge.admin.entities.VehicleMaster;
import com.weighbridge.admin.payloads.VehicleRequest;
import com.weighbridge.admin.payloads.VehicleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface VehicleMasterService {

    String addVehicle(VehicleRequest vehicleRequest, String transporterName);

    Page<VehicleResponse> vehicles(Pageable pageable);

    public VehicleResponse vehicleByNo(String vehicleNo);

    String updateVehicleByVehicleNo(String vehicleNo, VehicleRequest vehicleRequest);

    String deleteVehicleByVehicleNo(String vehicleNo);

}
