package com.weighbridge.admin.services.impls;

import com.weighbridge.admin.dtos.VehicleMasterDto;
import com.weighbridge.admin.entities.TransporterMaster;
import com.weighbridge.admin.entities.VehicleMaster;
import com.weighbridge.admin.exceptions.ResourceNotFoundException;
import com.weighbridge.admin.payloads.VehicleGateEntryResponse;
import com.weighbridge.admin.payloads.VehicleRequest;
import com.weighbridge.admin.payloads.VehicleResponse;
import com.weighbridge.admin.repsitories.TransporterMasterRepository;
import com.weighbridge.admin.repsitories.VehicleMasterRepository;
import com.weighbridge.admin.services.VehicleMasterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class VehicleMasterServiceImpl implements VehicleMasterService {

    @Autowired
    private VehicleMasterRepository vehicleMasterRepository;

    @Autowired
    private TransporterMasterRepository transporterMasterRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Override
    public String addVehicle(VehicleRequest vehicleRequest, String transporterName,String userId) {
        VehicleMaster existsVehicle = vehicleMasterRepository.findByVehicleNoAndTransporterMasterTransporterName(vehicleRequest.getVehicleNo(), transporterName);
        if (existsVehicle != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vehicle no: " + vehicleRequest.getVehicleNo() + " is already present with transporter name : " + transporterName);
        }
        TransporterMaster transporterMaster = transporterMasterRepository.findByTransporterName(transporterName);

        VehicleMaster vehicleMaster = transporterMasterRepository.findById(transporterMaster.getId()).map(transporter -> {
            VehicleMaster vm = vehicleMasterRepository.findByVehicleNo(vehicleRequest.getVehicleNo());
            long vehicleId = 0;
            if (vm != null) {
                vehicleId = vm.getId();
            }
            // vehicle is existed
            if (vehicleId != 0L) {
                VehicleMaster vehicle = vehicleMasterRepository.findById(vehicleId).orElseThrow(() -> new ResourceNotFoundException("Vehicle", "VehicleNo", vehicleRequest.getVehicleNo()));
                transporter.addVehicle(vehicle);
                transporterMasterRepository.save(transporter);
                return vehicle;
            }

            // Add and create a new vehicle
            VehicleMaster newVehicle = new VehicleMaster();
            newVehicle.setVehicleNo(vehicleRequest.getVehicleNo());
            newVehicle.setVehicleType(vehicleRequest.getVehicleType());
            newVehicle.setVehicleManufacturer(vehicleRequest.getVehicleManufacturer());
            newVehicle.setVehicleLoadCapacity(vehicleRequest.getVehicleLoadCapacity());
            newVehicle.setVehicleTareWeight(vehicleRequest.getVehicleTareWeight());
            newVehicle.setVehicleWheelsNo(vehicleRequest.getVehicleWheelsNo());
            newVehicle.setVehicleFitnessUpTo(vehicleRequest.getVehicleFitnessUpTo());

            // Get userId form session
         /*   HttpSession session = httpServletRequest.getSession();
            String userId;
            if (session != null && session.getAttribute("userId") != null) {
                userId = session.getAttribute("userId").toString();
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session Expired, Login again !");
            }*/
            LocalDateTime currentTime = LocalDateTime.now();
            newVehicle.setVehicleCreatedBy(userId);
            newVehicle.setVehicleCreatedDate(currentTime);
            newVehicle.setVehicleModifiedBy(userId);
            newVehicle.setVehicleModifiedDate(currentTime);

            // Save new vehicle information to db
            transporter.addVehicle(newVehicle);
            return vehicleMasterRepository.save(newVehicle);
        }).orElseThrow(() -> new ResourceNotFoundException("Transporter", "transporterName", transporterName));
        return "Vehicle added successfully";
    }


    @Override
    public Page<VehicleResponse> vehicles(Pageable pageable) {
        Page<VehicleMaster> responsePage = vehicleMasterRepository.findAll(pageable);
        Page<VehicleResponse> vehicleResponse = responsePage.map(vehicleMaster -> {

            return getVehicleResponse(vehicleMaster);
        });

        return vehicleResponse;
    }

    @Override
    public VehicleResponse vehicleByNo(String vehicleNo) {
        VehicleMaster vehicleMaster = vehicleMasterRepository.findByVehicleNo(vehicleNo);
        if (vehicleMaster == null) {
            throw new ResourceNotFoundException("Vehicle", "vehicle No", vehicleNo);
        }

        return getVehicleResponse(vehicleMaster);
    }

    @Override
    public String updateVehicleByVehicleNo(String vehicleNo, VehicleRequest vehicleRequest,String userId) {
        VehicleMaster vehicleMaster = vehicleMasterRepository.findByVehicleNo(vehicleNo);

        vehicleMaster.setVehicleWheelsNo(vehicleRequest.getVehicleWheelsNo());
        vehicleMaster.setVehicleType(vehicleRequest.getVehicleType());
        vehicleMaster.setVehicleManufacturer(vehicleRequest.getVehicleManufacturer());

        vehicleMaster.setVehicleFitnessUpTo(vehicleRequest.getVehicleFitnessUpTo());
        vehicleMaster.setVehicleLoadCapacity(vehicleRequest.getVehicleLoadCapacity());
        vehicleMaster.setVehicleTareWeight(vehicleRequest.getVehicleTareWeight());

        /*HttpSession session = httpServletRequest.getSession();
        String userId;
        if (session != null && session.getAttribute("userId") != null) {
            userId = session.getAttribute("userId").toString();
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session Expired, Login again !");
        }*/
        LocalDateTime currentTime = LocalDateTime.now();

        vehicleMaster.setVehicleModifiedBy(userId);
        vehicleMaster.setVehicleModifiedDate(currentTime);
        vehicleMasterRepository.save(vehicleMaster);

        return "Vehicle updated successfully";
    }

    @Override
    public String deleteVehicleByVehicleNo(String vehicleNo) {
        VehicleMaster vehicleMaster = vehicleMasterRepository.findByVehicleNo(vehicleNo);
        if (vehicleMaster == null) {
            throw new ResourceNotFoundException("Vehicle", "vehicle no", vehicleNo);
        }

        if (vehicleMaster.getVehicleStatus().equals("ACTIVE")) {
            vehicleMaster.setVehicleStatus("INACTIVE");
            vehicleMasterRepository.save(vehicleMaster);
            return "Vehicle deleted successfully";
        } else throw new ResourceNotFoundException("Vehicle", "vehicle no", vehicleNo);

    }

    @Override
    public VehicleGateEntryResponse getTransporterDetailByVehicle(String vehicleNo) {
        Set<Object[]> vehicleObject = vehicleMasterRepository.findVehicleInfoByVehicleNo(vehicleNo);
        // Process the result to aggregate transporter names into a single array
        List<String> transporterNames = new ArrayList<>();
        for (Object[] vehicleInfo : vehicleObject) {
            transporterNames.add((String) vehicleInfo[4]); // Assuming transporter name is at index 3
        }
        VehicleGateEntryResponse vehicleGateEntryResponse = new VehicleGateEntryResponse();
        vehicleGateEntryResponse.setVehicleNo(vehicleObject.iterator().next()[0].toString());
        vehicleGateEntryResponse.setVehicleWheelsNo((Integer) vehicleObject.iterator().next()[1]);
        vehicleGateEntryResponse.setVehicleFitnessUpTo((LocalDate) vehicleObject.iterator().next()[2]);
        vehicleGateEntryResponse.setVehicleType((String) vehicleObject.iterator().next()[3]);
        vehicleGateEntryResponse.setTransporter(transporterNames);
        return vehicleGateEntryResponse;

    }

    @Override
    public VehicleMasterDto getVehicleById(Long vehicleId) {
        VehicleMaster vehicleMaster = vehicleMasterRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", vehicleId.toString()));
        return modelMapper.map(vehicleMaster, VehicleMasterDto.class);
    }

    @Transactional
    @Override
    public String updateVehicleById(Long vehicleId, VehicleMasterDto vehicleMasterDto,String userId) {
        log.info("Updating vehicle wit ID: {}", vehicleId);
        VehicleMaster vehicleMaster = vehicleMasterRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", vehicleId.toString()));

      /*  HttpSession session = httpServletRequest.getSession();
        String userId;
        if (session != null && session.getAttribute("userId") != null) {
            userId = session.getAttribute("userId").toString();
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session Expired, Login again !");
        }*/
        LocalDateTime localDateTime=LocalDateTime.now();
        vehicleMaster.setVehicleType(vehicleMasterDto.getVehicleType());
        vehicleMaster.setVehicleManufacturer(vehicleMasterDto.getVehicleManufacturer());
        vehicleMaster.setVehicleWheelsNo(vehicleMasterDto.getVehicleWheelsNo());
        vehicleMaster.setVehicleTareWeight(vehicleMasterDto.getVehicleTareWeight());
        vehicleMaster.setVehicleLoadCapacity(vehicleMasterDto.getVehicleLoadCapacity());
        vehicleMaster.setVehicleFitnessUpTo(vehicleMasterDto.getVehicleFitnessUpTo());
        vehicleMaster.setVehicleModifiedBy(userId);
        vehicleMaster.setVehicleModifiedDate(localDateTime);
        try {
            vehicleMasterRepository.save(vehicleMaster);
        } catch (DataAccessException e) {
            log.error("Error updating vehicle details for ID: {}", vehicleId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error: Failed to update vehicle details", e);
        }
        log.info("Vehicle details updated successfully for ID: {}", vehicleId);
        return "Vehicle details updated successfully";
    }

    @Override
    public boolean deactivateVehicleById(Long vehicleId) {
        VehicleMaster vehicleMaster = getVehicleMasterById(vehicleId);

        if (vehicleMaster.getVehicleStatus().equals("ACTIVE")) {
            vehicleMaster.setVehicleStatus("INACTIVE");
            vehicleMasterRepository.save(vehicleMaster);
            return true;
        }
        return false;
    }

    private VehicleMaster getVehicleMasterById(Long vehicleId) {
        return vehicleMasterRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Transporter", "id", vehicleId.toString()));
    }

    @Override
    public boolean activateVehicleById(Long vehicleId) {
        VehicleMaster vehicleMaster = getVehicleMasterById(vehicleId);

        if (vehicleMaster.getVehicleStatus().equals("INACTIVE")) {
            vehicleMaster.setVehicleStatus("ACTIVE");
            vehicleMasterRepository.save(vehicleMaster);
            return true;
        }
        return false;
    }


    private VehicleResponse getVehicleResponse(VehicleMaster vehicleMaster) {
        VehicleResponse vehicleResponse = new VehicleResponse();
        vehicleResponse.setId(vehicleMaster.getId());
        vehicleResponse.setVehicleNo(vehicleMaster.getVehicleNo());
        vehicleResponse.setVehicleType(vehicleMaster.getVehicleType());
        vehicleResponse.setVehicleManufacturer(vehicleMaster.getVehicleManufacturer());
        vehicleResponse.setVehicleStatus(vehicleMaster.getVehicleStatus());

        Set<TransporterMaster> transporter = vehicleMaster.getTransporter();
        Set<String> strOfTransporter = new HashSet<>();
        for (TransporterMaster tm : transporter) {
            String transporterName = tm.getTransporterName();
            strOfTransporter.add(transporterName);
        }

        vehicleResponse.setTransporter(strOfTransporter);
        vehicleResponse.setFitnessUpto(vehicleMaster.getVehicleFitnessUpTo());
        return vehicleResponse;
    }
}