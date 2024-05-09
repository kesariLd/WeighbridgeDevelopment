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
import com.weighbridge.qualityuser.payloads.QualityRequest;
import com.weighbridge.qualityuser.payloads.QualityResponse;
import com.weighbridge.qualityuser.repository.QualityTransactioRepository;
import com.weighbridge.qualityuser.services.QualityTransactionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
      
        List<GateEntryTransaction> allTransactions = gateEntryTransactionRepository.findBySiteIdAndCompanyIdOrderByTicketNoDesc(userSite, userCompany);

        List<QualityResponse> qualityResponses = allTransactions.stream()
                .filter(transaction -> transaction.getTransactionType().equals("Inbound") || transaction.getTransactionType().equals("Outbound"))
                .flatMap(transaction -> {
                    VehicleTransactionStatus transactionStatus = vehicleTransactionStatusRepository.findByTicketNo(transaction.getTicketNo());
                    if (transactionStatus != null && (transactionStatus.getStatusCode().equals("GWT") || transactionStatus.getStatusCode().equals("TWT"))) {
                        QualityResponse qualityResponse = new QualityResponse();
                        qualityResponse.setTicketNo(transaction.getTicketNo());
                        qualityResponse.setTpNo(transaction.getTpNo());
                        qualityResponse.setPoNo(transaction.getPoNo());
                        qualityResponse.setChallanNo(transaction.getChallanNo());
                        qualityResponse.setTransactionType(transaction.getTransactionType());

                        SupplierMaster supplierMaster = supplierMasterRepository.findById(transaction.getSupplierId()).orElseThrow(() -> new ResourceNotFoundException("Supplier is not found"));
                        qualityResponse.setSupplierOrCustomerName(supplierMaster.getSupplierName());
                        qualityResponse.setSupplierOrCustomerAddress(supplierMaster.getSupplierAddressLine1());

                        MaterialMaster materialMaster = materialMasterRepository.findById(transaction.getMaterialId()).orElseThrow(() -> new ResourceNotFoundException("Material is not found"));
                        qualityResponse.setMaterialOrProduct(materialMaster.getMaterialName());
                        qualityResponse.setMaterialTypeOrProductType("materialType");

                        TransporterMaster transporterMaster = transporterMasterRepository.findById(transaction.getTransporterId()).orElseThrow(() -> new ResourceNotFoundException("Transporter is not found"));
                        qualityResponse.setTransporterName(transporterMaster.getTransporterName());

                        VehicleMaster vehicleMaster = vehicleMasterRepository.findById(transaction.getVehicleId()).orElseThrow(() -> new ResourceNotFoundException("Vehicle is not found"));
                        qualityResponse.setVehicleNo(vehicleMaster.getVehicleNo());
                        qualityResponse.setIn(transaction.getVehicleIn());
                        qualityResponse.setOut(transaction.getVehicleOut());
                        qualityResponse.setDate(transaction.getTransactionDate());

                        return Stream.of(qualityResponse);
                    } else {
                        return Stream.empty();
                    }
                })
                .collect(Collectors.toList());

        return qualityResponses;
    }


    @Override
    public String createQualityTransaction(Integer ticketNo, QualityRequest qualityRequest) {
        Optional<GateEntryTransaction> gateEntryTransactionOptional = gateEntryTransactionRepository.findById(ticketNo);
        if (gateEntryTransactionOptional.isPresent()) {

            QualityTransaction qualityTransaction = new QualityTransaction();
            qualityTransaction.setMoisture(qualityRequest.getMoisture());
            qualityTransaction.setFc(qualityRequest.getFc());
            qualityTransaction.setVm(qualityRequest.getVm());
            qualityTransaction.setAsh(qualityRequest.getAsh());
            qualityTransaction.setLoi(qualityRequest.getLoi());
            qualityTransaction.setFe_t(qualityRequest.getFe_t());
            qualityTransaction.setSize_03mm(qualityRequest.getSize_03mm());
            qualityTransaction.setSize_20mm(qualityRequest.getSize_20mm());
            qualityTransaction.setGateEntryTransaction(gateEntryTransactionOptional.get());

            log.info(qualityRequest.toString());
            log.info(qualityTransaction.toString());
            QualityTransaction savedQualityTransaction = qualityTransactioRepository.save(qualityTransaction);
            return "Quality added to ticket no : \"" + ticketNo + "\" successfully";
        } else {
            throw new ResourceNotFoundException("Gate Entry Transaction with ticketNo " + ticketNo + " not found");
        }
    }
}


