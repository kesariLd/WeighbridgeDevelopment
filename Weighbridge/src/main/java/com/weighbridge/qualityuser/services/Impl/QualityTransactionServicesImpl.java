package com.weighbridge.qualityuser.services.Impl;

import com.weighbridge.admin.entities.*;
import com.weighbridge.admin.exceptions.SessionExpiredException;
import com.weighbridge.admin.repsitories.*;
import com.weighbridge.gateuser.entities.GateEntryTransaction;
import com.weighbridge.gateuser.entities.VehicleTransactionStatus;
import com.weighbridge.gateuser.repositories.GateEntryTransactionRepository;
import com.weighbridge.gateuser.repositories.VehicleTransactionStatusRepository;
import com.weighbridge.qualityuser.entites.QualityTransaction;
import com.weighbridge.qualityuser.exception.ResourceNotFoundException;
import com.weighbridge.qualityuser.payloads.QualityResponse;
import com.weighbridge.qualityuser.repository.QualityTransactioRepository;
import com.weighbridge.qualityuser.services.QualityTransactionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class QualityTransactionServicesImpl implements QualityTransactionService {

    private final QualityTransactioRepository qualityTransactioRepository;
    private final GateEntryTransactionRepository gateEntryTransactionRepository;
    private final HttpServletRequest httpServletRequest;
    private final VehicleTransactionStatusRepository vehicleTransactionStatusRepository;

    private final SupplierMasterRepository supplierMasterRepository;

    private final CustomerMasterRepository customerMasterRepository;
    private final MaterialMasterRepository materialMasterRepository;
    private final TransporterMasterRepository transporterMasterRepository;
    private final VehicleMasterRepository vehicleMasterRepository;

    public QualityTransactionServicesImpl(QualityTransactioRepository qualityTransactioRepository,
                                          GateEntryTransactionRepository gateEntryTransactionRepository,
                                          HttpServletRequest httpServletRequest,
                                          VehicleTransactionStatusRepository vehicleTransactionStatusRepository,
                                          SupplierMasterRepository supplierMasterRepository, CustomerMasterRepository customerMasterRepository, MaterialMasterRepository materialMasterRepository, TransporterMasterRepository transporterMasterRepository, VehicleMasterRepository vehicleMasterRepository) {
        this.qualityTransactioRepository = qualityTransactioRepository;
        this.gateEntryTransactionRepository = gateEntryTransactionRepository;
        this.httpServletRequest = httpServletRequest;
        this.vehicleTransactionStatusRepository = vehicleTransactionStatusRepository;
        this.supplierMasterRepository = supplierMasterRepository;
        this.customerMasterRepository = customerMasterRepository;
        this.materialMasterRepository = materialMasterRepository;
        this.transporterMasterRepository = transporterMasterRepository;
        this.vehicleMasterRepository = vehicleMasterRepository;
    }

    @Override
    public List<QualityResponse> getAllGateDetails() {
        HttpSession session = httpServletRequest.getSession();
        String userId;
        String userCompany;
        String userSite;
        if (session != null && session.getAttribute("userId") != null) {
            userId = session.getAttribute("userId").toString();
            userSite = session.getAttribute("userSite").toString();
            userCompany = session.getAttribute("userCompany").toString();
        } else {
            throw new SessionExpiredException("Session Expired, Login again !");
        }

//        List<GateEntryTransaction> allTransactions = gateEntryTransactionRepository.findBySiteIdAndCompanyIdOrderByTicketNoDesc(userSite, userCompany);
        List<VehicleTransactionStatus> allGateTransactions = vehicleTransactionStatusRepository.findByStatusCode("GWT");

        List<GateEntryTransaction> transactionList = new ArrayList<>();
        allGateTransactions.forEach(vehicleTransactionStatus -> {
            log.info(String.valueOf(vehicleTransactionStatus.getTicketNo()));
            GateEntryTransaction transaction = gateEntryTransactionRepository.findByTicketNo(vehicleTransactionStatus.getTicketNo());
            transactionList.add(transaction);
        });

        List<QualityResponse> qualityResponses = transactionList.stream().map(gateEntryTransaction -> {
            QualityResponse qualityResponse = new QualityResponse();
            qualityResponse.setTicketNo(gateEntryTransaction.getTicketNo());
            qualityResponse.setTpNo(gateEntryTransaction.getTpNo());
            qualityResponse.setPoNo(gateEntryTransaction.getPoNo());
            qualityResponse.setChallanNo(gateEntryTransaction.getChallanNo());
            qualityResponse.setTransactionType(gateEntryTransaction.getTransactionType());

            SupplierMaster supplierMaster = supplierMasterRepository.findById(gateEntryTransaction.getSupplierId()).orElseThrow(() -> new ResourceNotFoundException("Supplier is not found"));
            qualityResponse.setSupplierOrCustomerName(supplierMaster.getSupplierName());
            qualityResponse.setSupplierOrCustomerAddress(supplierMaster.getSupplierAddressLine1());

            MaterialMaster materialMaster = materialMasterRepository.findById(gateEntryTransaction.getMaterialId()).orElseThrow(() -> new ResourceNotFoundException("Material is not found"));
            qualityResponse.setMaterial(materialMaster.getMaterialName());
            qualityResponse.setMaterialType("materialType");

            TransporterMaster transporterMaster = transporterMasterRepository.findById(gateEntryTransaction.getTransporterId()).orElseThrow(() -> new ResourceNotFoundException("Transporter is not found"));
            qualityResponse.setTransporterName(transporterMaster.getTransporterName());

            VehicleMaster vehicleMaster = vehicleMasterRepository.findById(gateEntryTransaction.getVehicleId()).orElseThrow(() -> new ResourceNotFoundException("Vehicle is not found"));
            qualityResponse.setVehicleNo(vehicleMaster.getVehicleNo());
            qualityResponse.setIn(gateEntryTransaction.getVehicleIn());
            qualityResponse.setOut(gateEntryTransaction.getVehicleOut());
            qualityResponse.setDate(gateEntryTransaction.getTransactionDate());
            return qualityResponse;
        }).collect(Collectors.toList());

        return qualityResponses;
    }
}

