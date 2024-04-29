package com.weighbridge.weighbridgeoperator.services.impls;

import com.weighbridge.admin.exceptions.ResourceNotFoundException;
import com.weighbridge.admin.exceptions.SessionExpiredException;
import com.weighbridge.gateuser.entities.GateEntryTransaction;
import com.weighbridge.gateuser.entities.TransactionLog;
import com.weighbridge.gateuser.entities.VehicleTransactionStatus;
import com.weighbridge.gateuser.repositories.GateEntryTransactionRepository;
import com.weighbridge.gateuser.repositories.TransactionLogRepository;
import com.weighbridge.gateuser.repositories.VehicleTransactionStatusRepository;
import com.weighbridge.weighbridgeoperator.entities.WeighmentTransaction;
import com.weighbridge.weighbridgeoperator.payloads.InboundWeighmentRequest;

import com.weighbridge.weighbridgeoperator.repositories.WeighmentTransactionRepository;
import com.weighbridge.weighbridgeoperator.services.WeighmentTransactionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WeighmentTransactionServiceImpl implements WeighmentTransactionService{
    @Autowired
    WeighmentTransactionRepository weighmentTransactionRepository;

    @Autowired
    GateEntryTransactionRepository gateEntryTransactionRepository;

    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    VehicleTransactionStatusRepository vehicleTransactionStatusRepository;

    @Autowired
    TransactionLogRepository transactionLogRepository;

    @Override
    public String inboundWeight(InboundWeighmentRequest weighmentRequest) {
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
        WeighmentTransaction byTicketTicketNo = weighmentTransactionRepository.findByTicketTicketNo(weighmentRequest.getTicketNo());
        System.out.println(byTicketTicketNo);
        if(byTicketTicketNo==null){
            WeighmentTransaction weighmentTransaction=new WeighmentTransaction();
            weighmentTransaction.setTicket(byId);
            weighmentTransaction.setMachineId(weighmentRequest.getMachineId());
            weighmentTransaction.setTemporaryWeight(weighmentRequest.getWeight());
            weighmentTransactionRepository.save(weighmentTransaction);

            //Vehiclestatus details
            VehicleTransactionStatus byTicketNo = vehicleTransactionStatusRepository.findByTicketNo(weighmentRequest.getTicketNo());
            byTicketNo.setStatusCode("1st");
            vehicleTransactionStatusRepository.save(byTicketNo);

            //History save with vehicle intime and vehicle out time
            TransactionLog transactionLog = new TransactionLog();
            transactionLog.setUserId(userId);
            transactionLog.setTicketNo(weighmentRequest.getTicketNo());
            transactionLog.setTimestamp(LocalDateTime.now());
            transactionLog.setStatusCode("1st");
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


            //Vehiclestatus details
            VehicleTransactionStatus byTicketNo = vehicleTransactionStatusRepository.findByTicketNo(weighmentRequest.getTicketNo());
            byTicketNo.setStatusCode("2nd");
            vehicleTransactionStatusRepository.save(byTicketNo);

            //History save with vehicle intime and vehicle out time
            TransactionLog transactionLog = new TransactionLog();
            transactionLog.setUserId(userId);
            transactionLog.setTicketNo(weighmentRequest.getTicketNo());
            transactionLog.setTimestamp(LocalDateTime.now());
            transactionLog.setStatusCode("2nd");
            transactionLogRepository.save(transactionLog);
            return "Second weight saved";
        }

    }
    
}