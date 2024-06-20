package com.weighbridge.admin.services;

import com.weighbridge.admin.dtos.VehicleMasterDto;
import com.weighbridge.admin.payloads.VehicleGateEntryResponse;
import com.weighbridge.admin.payloads.VehicleRequest;
import com.weighbridge.admin.payloads.VehicleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface VehicleMasterService {

    String addVehicle(VehicleRequest vehicleRequest, String transporterName,String userId);

    Page<VehicleResponse> vehicles(Pageable pageable);

    public VehicleResponse vehicleByNo(String vehicleNo);

    String updateVehicleByVehicleNo(String vehicleNo, VehicleRequest vehicleRequest,String userId);

    String deleteVehicleByVehicleNo(String vehicleNo);

    VehicleGateEntryResponse getTransporterDetailByVehicle(String vehicleNo);

    VehicleMasterDto getVehicleById(Long vehicleId);

    String updateVehicleById(Long vehicleId, VehicleMasterDto vehicleDto,String userId);

    boolean deactivateVehicleById(Long vehicleId);

    boolean activateVehicleById(Long vehicleId);
}
