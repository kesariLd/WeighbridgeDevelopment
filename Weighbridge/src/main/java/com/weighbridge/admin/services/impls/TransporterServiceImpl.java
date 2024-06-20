package com.weighbridge.admin.services.impls;

import com.weighbridge.admin.dtos.TransporterDto;
import com.weighbridge.admin.exceptions.ResourceNotFoundException;
import com.weighbridge.admin.payloads.TransporterRequest;
import com.weighbridge.admin.repsitories.TransporterMasterRepository;
import com.weighbridge.admin.services.TransporterService;
import com.weighbridge.admin.entities.TransporterMaster;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TransporterServiceImpl implements TransporterService {
    @Autowired
    private TransporterMasterRepository transporterMasterRepository;
    @Autowired
    private  ModelMapper modelMapper;
    @Autowired
    private HttpServletRequest request;

    @Override
    public String addTransporter(TransporterRequest transporterRequest,String userId) {
        Boolean ByTransporterMaster = transporterMasterRepository.existsByTransporterName(transporterRequest.getTransporterName());
        if (ByTransporterMaster){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Transporter already exist");
        }
        else {
           /* HttpSession session = request.getSession();
            String userId;
            if (session != null && session.getAttribute("userId") != null) {
                userId = session.getAttribute("userId").toString();
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session Expired, Login again !");
            }*/
            TransporterMaster transporterMaster=new TransporterMaster();
            transporterMaster.setTransporterName(transporterRequest.getTransporterName());
            transporterMaster.setTransporterAddress(transporterRequest.getTransporterAddress());
            transporterMaster.setTransporterContactNo(transporterRequest.getTransporterContactNo());
            transporterMaster.setTransporterEmailId(transporterRequest.getTransporterEmailId());

            LocalDateTime currentDateTime = LocalDateTime.now();

            transporterMaster.setTransporterCreatedBy(userId);
            transporterMaster.setTransporterCreatedDate(currentDateTime);
            transporterMaster.setTransporterModifiedBy(userId);
            transporterMaster.setTransporterModifiedDate(currentDateTime);


            transporterMasterRepository.save(transporterMaster);
            return "Transporter added successfully";

        }

    }


    @Override
    public List<String> getAllTransporterNames() {
        return transporterMasterRepository.findAllByTransporterStatus();
    }

    @Override
    public List<TransporterDto> getAllTransporter() {
        List<TransporterMaster> transporterMasters = transporterMasterRepository.findAll();
        return transporterMasters.stream()
                .map(transporterMaster -> modelMapper.map(transporterMaster, TransporterDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public TransporterDto getTransporterById(Long transporterId) {
        TransporterMaster transporterMaster = transporterMasterRepository.findById(transporterId)
                .orElseThrow(() -> new ResourceNotFoundException("Transporter", "id", transporterId.toString()));
        return modelMapper.map(transporterMaster, TransporterDto.class);
    }

    @Transactional
    @Override
    public String updateTransporterById(Long transporterId, TransporterDto transporterDto,String userId) {
        log.info("Updating transporter wit ID: {}", transporterId);
        TransporterMaster transporterMaster = transporterMasterRepository.findById(transporterId)
                .orElseThrow(() -> new ResourceNotFoundException("Transporter", "id", transporterId.toString()));

      /*  HttpSession session = request.getSession();
        String userId;
        if (session != null && session.getAttribute("userId") != null) {
            userId = session.getAttribute("userId").toString();
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session Expired, Login again !");
        }*/

        transporterMaster.setTransporterName(transporterDto.getTransporterName());
        transporterMaster.setTransporterContactNo(transporterDto.getTransporterContactNo());
        transporterMaster.setTransporterEmailId(transporterDto.getTransporterEmailId());
        transporterMaster.setTransporterAddress(transporterDto.getTransporterAddress());
        transporterMaster.setTransporterModifiedBy(userId);
        transporterMaster.setTransporterModifiedDate(LocalDateTime.now());
        try {
            transporterMasterRepository.save(transporterMaster);
        } catch (DataAccessException e) {
            log.error("Error updating transporter details for ID: {}", transporterId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error: Failed to update transporter details", e);
        }
        log.info("Transporter details updated successfully for ID: {}", transporterId);
        return "Transporter details updated successfully";
    }

    @Override
    public boolean deactivateTransporterById(Long transporterId) {
        TransporterMaster transporterMaster = getTransporterMasterById(transporterId);

        if (transporterMaster.getStatus().equals("ACTIVE")) {
            transporterMaster.setStatus("INACTIVE");
            transporterMasterRepository.save(transporterMaster);
            return true;
        }
        return false;
    }

    @Override
    public boolean activateTransporterById(Long transporterId) {
        TransporterMaster transporterMaster = getTransporterMasterById(transporterId);

        if (transporterMaster.getStatus().equals("INACTIVE")) {
            transporterMaster.setStatus("ACTIVE");
            transporterMasterRepository.save(transporterMaster);
            return true;
        }
        return false;
    }

    private TransporterMaster getTransporterMasterById(Long transporterId) {
        return transporterMasterRepository.findById(transporterId)
                .orElseThrow(() -> new ResourceNotFoundException("Transporter", "id", transporterId.toString()));
    }

}
