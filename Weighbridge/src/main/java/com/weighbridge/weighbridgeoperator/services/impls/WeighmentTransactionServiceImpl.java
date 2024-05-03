package com.weighbridge.weighbridgeoperator.services.impls;

import com.weighbridge.admin.entities.VehicleMaster;
import com.weighbridge.admin.exceptions.ResourceNotFoundException;
import com.weighbridge.admin.exceptions.SessionExpiredException;
import com.weighbridge.admin.repsitories.MaterialMasterRepository;
import com.weighbridge.admin.repsitories.SupplierMasterRepository;
import com.weighbridge.admin.repsitories.TransporterMasterRepository;
import com.weighbridge.admin.repsitories.VehicleMasterRepository;
import com.weighbridge.gateuser.entities.GateEntryTransaction;
import com.weighbridge.gateuser.entities.TransactionLog;
import com.weighbridge.gateuser.entities.VehicleTransactionStatus;
import com.weighbridge.gateuser.repositories.GateEntryTransactionRepository;
import com.weighbridge.gateuser.repositories.TransactionLogRepository;
import com.weighbridge.gateuser.repositories.VehicleTransactionStatusRepository;
import com.weighbridge.weighbridgeoperator.entities.WeighmentTransaction;


import com.weighbridge.weighbridgeoperator.payloads.TicketResponse;
import com.weighbridge.weighbridgeoperator.payloads.WeighmentRequest;
import com.weighbridge.weighbridgeoperator.payloads.WeighmentTransactionResponse;
import com.weighbridge.weighbridgeoperator.repositories.WeighmentTransactionRepository;
import com.weighbridge.weighbridgeoperator.services.WeighmentTransactionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class WeighmentTransactionServiceImpl implements WeighmentTransactionService{
    @Autowired
    private WeighmentTransactionRepository weighmentTransactionRepository;

    @Autowired
    private GateEntryTransactionRepository gateEntryTransactionRepository;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private VehicleTransactionStatusRepository vehicleTransactionStatusRepository;

    @Autowired
    private TransactionLogRepository transactionLogRepository;

    @Autowired
    private MaterialMasterRepository materialMasterRepository;

    @Autowired
    private SupplierMasterRepository supplierMasterRepository;

    @Autowired
    private TransporterMasterRepository transporterMasterRepository;

    @Autowired
    private VehicleMasterRepository vehicleMasterRepository;

    @Override
    public String saveWeight(WeighmentRequest weighmentRequest) {
        // Set user session details
        HttpSession session = httpServletRequest.getSession();
        String userId;
        String userCompany;
        String userSite;
        if (session != null && session.getAttribute("userId") != null) {
            userId = session.getAttribute("userId").toString();
            userSite = session.getAttribute("userSite").toString();
            userCompany = session.getAttribute("userCompany").toString();
        }
        else {
            throw new SessionExpiredException("Session Expired, Login again !");
        }
        GateEntryTransaction byId = gateEntryTransactionRepository.findById(weighmentRequest.getTicketNo()).get();
        WeighmentTransaction byTicketTicketNo = weighmentTransactionRepository.findByGateEntryTransactionTicketNo(weighmentRequest.getTicketNo());
        VehicleTransactionStatus byTicketNo = vehicleTransactionStatusRepository.findByTicketNo(weighmentRequest.getTicketNo());
        if(byTicketTicketNo==null){
            WeighmentTransaction weighmentTransaction=new WeighmentTransaction();
            weighmentTransaction.setGateEntryTransaction(byId);
            weighmentTransaction.setMachineId(weighmentRequest.getMachineId());
            weighmentTransaction.setTemporaryWeight(weighmentRequest.getWeight());
            weighmentTransactionRepository.save(weighmentTransaction);

            //History save with vehicle intime and vehicle out time
            TransactionLog transactionLog = new TransactionLog();
            transactionLog.setUserId(userId);
            transactionLog.setTicketNo(weighmentRequest.getTicketNo());
            transactionLog.setTimestamp(LocalDateTime.now());

            if(byId.getTransactionType().equalsIgnoreCase("Inbound")) {
                byTicketNo.setStatusCode("GWT");
                transactionLog.setStatusCode("GWT");
            }
            else{
                byTicketNo.setStatusCode("TWT");
                transactionLog.setStatusCode("TWT");
            }
            vehicleTransactionStatusRepository.save(byTicketNo);
            transactionLogRepository.save(transactionLog);
            return "First Weight saved.";
        }
        else {
            double temporaryWeight = byTicketTicketNo.getTemporaryWeight();
            if (temporaryWeight>weighmentRequest.getWeight()){
                byTicketTicketNo.setGrossWeight(temporaryWeight);
                byTicketTicketNo.setTareWeight(weighmentRequest.getWeight());
            }
            else{
               byTicketTicketNo.setTareWeight(temporaryWeight);
               byTicketTicketNo.setGrossWeight(weighmentRequest.getWeight());
            }
            double netWeight = Math.abs(temporaryWeight - weighmentRequest.getWeight());
            byTicketTicketNo.setNetWeight(netWeight);
            weighmentTransactionRepository.save(byTicketTicketNo);


            //History save with vehicle intime and vehicle out time
            TransactionLog transactionLog = new TransactionLog();
            transactionLog.setUserId(userId);
            transactionLog.setTicketNo(weighmentRequest.getTicketNo());
            transactionLog.setTimestamp(LocalDateTime.now());

            //Vehiclestatus details
            if(byTicketNo.getStatusCode().equalsIgnoreCase("GWT")) {
                byTicketNo.setStatusCode("TWT");
                transactionLog.setStatusCode("TWT");
            }
            else {
                byTicketNo.setStatusCode("GWT");
                transactionLog.setStatusCode("GWT");
            }
            vehicleTransactionStatusRepository.save(byTicketNo);
            transactionLogRepository.save(transactionLog);
            return "Second weight saved";
        }
    }

    @Override
    public List<WeighmentTransactionResponse> getAllGateDetails() {
        HttpSession session = httpServletRequest.getSession();
        String userId;
        String userCompany;
        String userSite;
        if (session != null && session.getAttribute("userId") != null) {
            userId = session.getAttribute("userId").toString();
            userSite = session.getAttribute("userSite").toString();
            userCompany = session.getAttribute("userCompany").toString();
        }
        else {
            throw new SessionExpiredException("Session Expired, Login again !");
        }
        List<Object[]> allUsers=weighmentTransactionRepository.getAllGateEntries(userSite);
        List<WeighmentTransactionResponse> responses = new ArrayList<>();
        if(allUsers==null){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"No gateEntries yet.");
        }
        else {
            try {
                for (Object[] row : allUsers) {
                    WeighmentTransactionResponse response = new WeighmentTransactionResponse();
                    response.setTicketNo(String.valueOf(row[0]));
                    TransactionLog byTicketNo = transactionLogRepository.findByTicketNoAndStatusCode((Integer) row[0], "GWT");
                    TransactionLog byTicketNo2 = transactionLogRepository.findByTicketNoAndStatusCode((Integer) row[0], "TWT");
                    LocalDateTime timestamp = null, timestamp1 = null,resTimeStamp=null,resTimeStamp1=null;
                    if (byTicketNo != null) {
                        timestamp = byTicketNo.getTimestamp();
                        resTimeStamp = timestamp.withSecond(0).withNano(0);
                    }
                    if (byTicketNo2 != null) {
                        timestamp1 = byTicketNo2.getTimestamp();
                        resTimeStamp1=timestamp1.withSecond(0).withNano(0);
                    }
                    response.setWeighmentNo(String.valueOf(row[1]));
                    response.setTransactionType((String) row[2]);
                    response.setTransactionDate((LocalDate) row[3]);
                    if (((String) row[2]).equalsIgnoreCase("Inbound")) {
                        response.setGrossWeight(String.valueOf(row[7]) + "/" + resTimeStamp);
                        response.setTareWeight(row[5] + "/" + resTimeStamp1);
                    } else {
                        response.setTareWeight(String.valueOf(row[7]) + "/" + resTimeStamp1);
                        response.setGrossWeight(row[4] + "/" + resTimeStamp);
                    }
                    response.setNetWeight(String.valueOf(row[6] + "/" + resTimeStamp1));
                    response.setVehicleNo((String) row[8]);
                    response.setVehicleFitnessUpTo((LocalDate) row[9]);
                    response.setSupplierName((String) row[10]);
                    response.setTransporterName((String) row[11]);
                    response.setMaterialName((String) row[12]);
                    // Set other fields similarly
                    responses.add(response);
                }

            }
            catch (Exception e){
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
            }
            return responses;
        }
    }

    @Override
    public TicketResponse getResponseByTicket(Integer ticketNo) {
        GateEntryTransaction gateEntryTransaction=gateEntryTransactionRepository.findById(ticketNo).get();
        if(gateEntryTransaction==null){
            throw new ResourceNotFoundException("ticket","ticketNo",ticketNo.toString());
        }
        else{
           TicketResponse ticketResponse=new TicketResponse();
           ticketResponse.setPoNo(gateEntryTransaction.getPoNo());
           ticketResponse.setTpN0(gateEntryTransaction.getTpNo());
           ticketResponse.setChallanNo(gateEntryTransaction.getChallanNo());
           ticketResponse.setMaterial(materialMasterRepository.findMaterialNameByMaterialId(gateEntryTransaction.getMaterialId()));
           ticketResponse.setTransporter(transporterMasterRepository.findTransporterNameByTransporterId(gateEntryTransaction.getTransporterId()));
           ticketResponse.setDriverName(gateEntryTransaction.getDriverName());
           VehicleMaster vehicleMaster = vehicleMasterRepository.findById(gateEntryTransaction.getVehicleId()).orElseThrow(() -> new ResourceNotFoundException("Vehicle is not found"));

            ticketResponse.setVehicleNo(vehicleMaster.getVehicleNo());
           ticketResponse.setSupplier(supplierMasterRepository.findSupplierNameBySupplierId(gateEntryTransaction.getSupplierId()).toString());
           ticketResponse.setDriverDlNo(gateEntryTransaction.getDlNo());
           ticketResponse.setSupplierAddress(supplierMasterRepository.findSupplierAddressBySupplierName(supplierMasterRepository.findSupplierNameBySupplierId(gateEntryTransaction.getSupplierId()).toString()).toString());
           return ticketResponse;
        }
    }
}