package com.weighbridge.admin.services.impls;

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
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class VehicleMasterServiceImpl implements VehicleMasterService {

    @Autowired
    private VehicleMasterRepository vehicleMasterRepository;

    @Autowired
    private TransporterMasterRepository transporterMasterRepository;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private HttpServletRequest httpServletRequest;

//    @Override
//    public String addVehicle(VehicleMasterDto vehicleMasterDto) {
//        VehicleMaster vehicleMaster=vehicleMasterRepository.findByVehicleNo(vehicleMasterDto.getVehicleNo());
//        HttpSession session = request.getSession();
//        LocalDateTime currentDateTime = LocalDateTime.now();
//        if(vehicleMaster==null) {
//            VehicleMaster vehicleMasters = new VehicleMaster();
//            vehicleMasters.setCreatedDate(currentDateTime);
//            vehicleMasters.setModifiedDate(currentDateTime);
//            vehicleMasters.setCreatedBy(session.getAttribute("userId").toString());
//            vehicleMasters.setModifiedBy(session.getAttribute("userId").toString());
//            vehicleMasters.setVehicleNo(vehicleMasterDto.getVehicleNo());
//
//            //assigning transporter to vehicle
//            List<TransporterMaster> transporterMasterList = new ArrayList<>();
//            transporterMasterList.addAll(getAllTransportMaster(vehicleMasterDto.getVehicleNo()));
//            //  System.out.println("--------"+getAllTransportMaster(vehicleMasterDto.getVehicleNo()));
//            TransporterMaster byTransporterName = transporterMasterRepository.findByTransporterName(vehicleMasterDto.getTransporterMaster());
//            transporterMasterList.add(byTransporterName);
//            vehicleMasters.setTransporter(transporterMasterList);
////        transporterMasterList.add(transporterMasterRepository.findByTransporterName("Maa tarini transporter"));
//
//            vehicleMasters.setVehicleManufacturer(vehicleMasterDto.getVehicleManufacturer());
//            vehicleMasters.setVehicleType(vehicleMasterDto.getVehicleType());
//            vehicleMasters.setLoadCapacity(vehicleMasterDto.getLoadCapacity());
//            vehicleMasters.setFitnessUpto(vehicleMasterDto.getFitnessUpto());
//            vehicleMasters.setWheelsNo(vehicleMasterDto.getWheelsNo());
//            vehicleMasters.setTareWeight(vehicleMasterDto.getTareWeight());
//            vehicleMasterRepository.save(vehicleMasters);
//            return "Vehicle added successfully";
//        }
//        else{
//            //assigning only transporter
//            List<TransporterMaster> transporterMasterList = new ArrayList<>();
//            transporterMasterList.addAll(getAllTransportMaster(vehicleMasterDto.getVehicleNo()));
//            //  System.out.println("--------"+getAllTransportMaster(vehicleMasterDto.getVehicleNo()));
//            TransporterMaster byTransporterName = transporterMasterRepository.findByTransporterName(vehicleMasterDto.getTransporterMaster());
//            transporterMasterList.add(byTransporterName);
//            vehicleMaster.setTransporter(transporterMasterList);
//            vehicleMaster.setModifiedBy(session.getAttribute("userId").toString());
//            vehicleMaster.setModifiedDate(currentDateTime);
//            vehicleMasterRepository.save(vehicleMaster);
//            return "transporter updated";
//        }
//    }

    private List<TransporterMaster> getAllTransportMaster(String vehicleNo) {
        return vehicleMasterRepository.findTransportersByVehicleId(vehicleNo);
    }

    @Override
    public String addVehicle(VehicleRequest vehicleRequest, String transporterName) {
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
            HttpSession session = httpServletRequest.getSession();
            String userId;
            if (session != null && session.getAttribute("userId") != null) {
                userId = session.getAttribute("userId").toString();
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session Expired, Login again !");
            }
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
    public String updateVehicleByVehicleNo(String vehicleNo, VehicleRequest vehicleRequest) {
        VehicleMaster vehicleMaster = vehicleMasterRepository.findByVehicleNo(vehicleNo);

        vehicleMaster.setVehicleWheelsNo(vehicleRequest.getVehicleWheelsNo());
        vehicleMaster.setVehicleType(vehicleRequest.getVehicleType());
        vehicleMaster.setVehicleManufacturer(vehicleRequest.getVehicleManufacturer());

        vehicleMaster.setVehicleFitnessUpTo(vehicleRequest.getVehicleFitnessUpTo());
        vehicleMaster.setVehicleLoadCapacity(vehicleRequest.getVehicleLoadCapacity());
        vehicleMaster.setVehicleTareWeight(vehicleRequest.getVehicleTareWeight());

        HttpSession session = httpServletRequest.getSession();
        String userId;
        if (session != null && session.getAttribute("userId") != null) {
            userId = session.getAttribute("userId").toString();
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session Expired, Login again !");
        }
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



    private VehicleResponse getVehicleResponse(VehicleMaster vehicleMaster) {
        VehicleResponse vehicleResponse = new VehicleResponse();
        vehicleResponse.setVehicleNo(vehicleMaster.getVehicleNo());
        vehicleResponse.setVehicleType(vehicleMaster.getVehicleType());
        vehicleResponse.setVehicleManufacturer(vehicleMaster.getVehicleManufacturer());


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